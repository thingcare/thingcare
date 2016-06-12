package io.thingcare.modules.security;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.thingcare.api.security.user.CreateUserCommand;
import io.thingcare.api.security.user.ManagedUserDto;
import io.thingcare.api.security.user.UserCommonConstants;
import io.thingcare.api.web.util.HeaderUtil;
import io.thingcare.api.web.util.PaginationUtil;
import io.thingcare.modules.security.authority.AuthoritiesConstants;
import io.thingcare.modules.security.authority.Authority;
import io.thingcare.modules.security.authority.AuthorityRepository;
import io.thingcare.modules.security.user.ManagedUserMapper;
import io.thingcare.modules.security.user.User;
import io.thingcare.modules.security.user.UserRepository;
import io.thingcare.modules.security.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing users.
 * <p>
 * <p>
 * This class accesses the User entity, and needs to fetch its collection of authorities.
 * </p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li>Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li>As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private UserService userService;
	@Autowired
	private ManagedUserMapper managedUserMapper;

	@Autowired
	private CommandGateway commandGateway;

	/**
	 * POST /users : Creates a new user.
	 * <p>
	 * Creates a new user if the login and email are not already used, and sends an
	 * mail with an activation link.
	 * The user needs to be activated on creation.
	 * </p>
	 *
	 * @param managedUserDto the user to create
	 * @param request        the HTTP request
	 * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request)
	 * if the login or email is already in use
	 * @throws URISyntaxException if the Location URI syntaxt is incorrect
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<?> createUser(@RequestBody ManagedUserDto managedUserDto, HttpServletRequest request)
			throws URISyntaxException {
		log.debug("REST request to save User : {}", managedUserDto);
		User newUser = commandGateway.sendAndWait(CreateUserCommand.builder().managedUserDto(managedUserDto).build());
		return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
				.headers(HeaderUtil.createAlert("userManagement.created", newUser.getLogin())).body(newUser);
	}

	/**
	 * PUT /users : Updates an existing User.
	 *
	 * @param managedUserDto the user to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated user,
	 * or with status 400 (Bad Request) if the login or email is already in use,
	 * or with status 500 (Internal Server Error) if the user couldnt be updated
	 */
	@RequestMapping(value = "/users", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<ManagedUserDto> updateUser(@RequestBody ManagedUserDto managedUserDto) {
		log.debug("REST request to update User : {}", managedUserDto);
		Optional<User> existingUser = userRepository.findOneByEmail(managedUserDto.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDto.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "E-mail already in use"))
					.body(null);
		}
		existingUser = userRepository.findOneByLogin(managedUserDto.getLogin().toLowerCase());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDto.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use"))
					.body(null);
		}
		return userRepository.findOneById(managedUserDto.getId()).map(user -> {
			user.setLogin(managedUserDto.getLogin());
			user.setFirstName(managedUserDto.getFirstName());
			user.setLastName(managedUserDto.getLastName());
			user.setEmail(managedUserDto.getEmail());
			user.setActivated(managedUserDto.isActivated());
			user.setLangKey(managedUserDto.getLangKey());
			Set<Authority> authorities = user.getAuthorities();
			authorities.clear();
			managedUserDto.getAuthorities().stream()
					.forEach(authority -> authorities.add(authorityRepository.findOne(authority)));
			userRepository.save(user);
			return ResponseEntity.ok()
					.headers(HeaderUtil.createAlert("userManagement.updated", managedUserDto.getLogin()))
					.body(managedUserMapper.asDto(userRepository.findOne(managedUserDto.getId())));
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /users : get all users.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all users
	 * @throws URISyntaxException if the pagination headers couldnt be generated
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ManagedUserDto>> getAllUsers(Pageable pageable) throws URISyntaxException {
		Page<User> page = userRepository.findAll(pageable);
		List<ManagedUserDto> managedUserDtos = page.getContent().stream().map(managedUserMapper::asDto)
				.collect(Collectors.toList());
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
		return new ResponseEntity<>(managedUserDtos, headers, HttpStatus.OK);
	}

	/**
	 * GET /users/:login : get the "login" user.
	 *
	 * @param login the login of the user to find
	 * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/users/{login:" + UserCommonConstants.LOGIN_REGEX
			+ "}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ManagedUserDto> getUser(@PathVariable String login) {
		log.debug("REST request to get User : {}", login);
		return userService.getUserWithAuthoritiesByLogin(login).map(managedUserMapper::asDto)
				.map(managedUserDTO -> new ResponseEntity<>(managedUserDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE USER :login : delete the "login" User.
	 *
	 * @param login the login of the user to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/users/{login:" + UserCommonConstants.LOGIN_REGEX
			+ "}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> deleteUser(@PathVariable String login) {
		log.debug("REST request to delete User: {}", login);
		userService.deleteUserInformation(login);
		return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.deleted", login)).build();
	}
}
