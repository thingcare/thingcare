package io.thingcare.modules.security;

import io.thingcare.api.security.user.KeyAndPasswordDto;
import io.thingcare.api.security.user.ManagedUserDto;
import io.thingcare.api.security.user.UserCommonConstants;
import io.thingcare.api.security.user.UserDto;
import io.thingcare.api.web.util.HeaderUtil;
import io.thingcare.modules.security.user.User;
import io.thingcare.modules.security.user.UserMapper;
import io.thingcare.modules.security.user.UserRepository;
import io.thingcare.modules.security.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountResource {

	private final Logger log = LoggerFactory.getLogger(AccountResource.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<?> registerAccount(@Valid @RequestBody ManagedUserDto managedUserDto,
			HttpServletRequest request) {

		HttpHeaders textPlainHeaders = new HttpHeaders();
		textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);

		return userRepository	.findOneByLogin(managedUserDto	.getLogin()
																.toLowerCase())
								.map(user -> new ResponseEntity<>("login already in use", textPlainHeaders,
										HttpStatus.BAD_REQUEST))
								.orElseGet(() -> userRepository	.findOneByEmail(managedUserDto.getEmail())
																.map(user -> new ResponseEntity<>(
																		"e-mail address already in use",
																		textPlainHeaders, HttpStatus.BAD_REQUEST))
																.orElseGet(() -> {
																	User user = userService.createUserInformation(
																			managedUserDto.getLogin(),
																			managedUserDto.getPassword(),
																			managedUserDto.getFirstName(),
																			managedUserDto.getLastName(),
																			managedUserDto	.getEmail()
																							.toLowerCase(),
																			managedUserDto.getLangKey());
																	String baseUrl = request.getScheme() + // "http"
																	"://" + // "://"
																	request.getServerName() + // "myhost"
																	":" + // ":"
																	request.getServerPort() + // "80"
																	request.getContextPath(); // "/myContextPath" or ""
																								// if deployed in root
																								// context

																	// mailService.sendActivationEmail(user, baseUrl);
																	return new ResponseEntity<>(HttpStatus.CREATED);
																}));
	}

	@RequestMapping(value = "/activate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
		return userService	.activateRegistration(key)
							.map(user -> new ResponseEntity<String>(HttpStatus.OK))
							.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	@RequestMapping(value = "/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDto> getAccount() {
		return Optional	.ofNullable(userService.getUserWithAuthorities())
						.map(user -> new ResponseEntity<>(userMapper.asDto(user), HttpStatus.OK))
						.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@RequestMapping(value = "/account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveAccount(@Valid @RequestBody UserDto userDto) {
		Optional<User> existingUser = userRepository.findOneByEmail(userDto.getEmail());
		if (existingUser.isPresent() && (!existingUser	.get()
														.getLogin()
														.equalsIgnoreCase(userDto.getLogin()))) {
			return ResponseEntity	.badRequest()
									.headers(HeaderUtil.createFailureAlert("user-management", "emailexists",
											"Email already in use"))
									.body(null);
		}
		return userRepository	.findOneByLogin(SecurityUtils.getCurrentUserLogin())
								.map(u -> {
									userService.updateUserInformation(userDto.getFirstName(), userDto.getLastName(),
											userDto.getEmail(), userDto.getLangKey());
									return new ResponseEntity<String>(HttpStatus.OK);
								})
								.orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@RequestMapping(value = "/account/change_password", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> changePassword(@RequestBody String password) {
		if (!checkPasswordLength(password)) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		userService.changePassword(password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/account/reset_password/init", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
		return userService	.requestPasswordReset(mail)
							.map(user -> {
								String baseUrl = request.getScheme() + "://" + request.getServerName() + ":"
										+ request.getServerPort() + request.getContextPath();
								// mailService.sendPasswordResetMail(user, baseUrl);
								return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
							})
							.orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/account/reset_password/finish", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordDto keyAndPassword) {
		if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		return userService	.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
							.map(user -> new ResponseEntity<String>(HttpStatus.OK))
							.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private boolean checkPasswordLength(String password) {
		return (!StringUtils.isEmpty(password) && password.length() >= UserCommonConstants.PASSWORD_MIN_LENGTH
				&& password.length() <= UserCommonConstants.PASSWORD_MAX_LENGTH);
	}
}
