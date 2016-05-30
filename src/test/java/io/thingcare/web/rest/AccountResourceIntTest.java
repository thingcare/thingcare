package io.thingcare.web.rest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.thingcare.ThingCareApplication;
import io.thingcare.api.security.user.ManagedUserDto;
import io.thingcare.api.security.user.UserDto;
import io.thingcare.core.MailService;
import io.thingcare.modules.security.AccountResource;
import io.thingcare.modules.security.authority.AuthoritiesConstants;
import io.thingcare.modules.security.authority.Authority;
import io.thingcare.modules.security.authority.AuthorityRepository;
import io.thingcare.modules.security.user.User;
import io.thingcare.modules.security.user.UserRepository;
import io.thingcare.modules.security.user.UserService;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ThingCareApplication.class)
@WebAppConfiguration
@IntegrationTest
public class AccountResourceIntTest {

	@Inject
	private UserRepository userRepository;

	@Inject
	private AuthorityRepository authorityRepository;

	@Inject
	private UserService userService;

	@Mock
	private UserService mockUserService;

	@Mock
	private MailService mockMailService;

	private MockMvc restUserMockMvc;

	private MockMvc restMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		doNothing()	.when(mockMailService)
					.sendActivationEmail((User) anyObject(), anyString());

		AccountResource accountResource = new AccountResource();
		ReflectionTestUtils.setField(accountResource, "userRepository", userRepository);
		ReflectionTestUtils.setField(accountResource, "userService", userService);
		ReflectionTestUtils.setField(accountResource, "mailService", mockMailService);

		AccountResource accountUserMockResource = new AccountResource();
		ReflectionTestUtils.setField(accountUserMockResource, "userRepository", userRepository);
		ReflectionTestUtils.setField(accountUserMockResource, "userService", mockUserService);
		ReflectionTestUtils.setField(accountUserMockResource, "mailService", mockMailService);

		this.restMvc = MockMvcBuilders	.standaloneSetup(accountResource)
										.build();
		this.restUserMockMvc = MockMvcBuilders	.standaloneSetup(accountUserMockResource)
												.build();
	}

	@Test
	public void testNonAuthenticatedUser() throws Exception {
		restUserMockMvc	.perform(get("/api/authenticate").accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().string(""));
	}

	@Test
	public void testAuthenticatedUser() throws Exception {
		restUserMockMvc	.perform(get("/api/authenticate")	.with(request -> {
			request.setRemoteUser("test");
			return request;
		})
															.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().string("test"));
	}

	@Test
	public void testGetExistingAccount() throws Exception {
		Set<Authority> authorities = new HashSet<>();
		Authority authority = new Authority();
		authority.setName(AuthoritiesConstants.ADMIN);
		authorities.add(authority);

		User user = new User();
		user.setLogin("test");
		user.setFirstName("john");
		user.setLastName("doe");
		user.setEmail("john.doe@jhipter.com");
		user.setAuthorities(authorities);
		when(mockUserService.getUserWithAuthorities()).thenReturn(user);

		restUserMockMvc	.perform(get("/api/account").accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.login").value("test"))
						.andExpect(jsonPath("$.firstName").value("john"))
						.andExpect(jsonPath("$.lastName").value("doe"))
						.andExpect(jsonPath("$.email").value("john.doe@jhipter.com"))
						.andExpect(jsonPath("$.authorities").value(AuthoritiesConstants.ADMIN));
	}

	@Test
	public void testGetUnknownAccount() throws Exception {
		when(mockUserService.getUserWithAuthorities()).thenReturn(null);

		restUserMockMvc	.perform(get("/api/account").accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isInternalServerError());
	}

	@Test
	public void testRegisterValid() throws Exception {
		ManagedUserDto validUser = new ManagedUserDto(null, // id
				"joe", // login
				"password", // password
				"Joe", // firstName
				"Shmoe", // lastName
				"joe@example.com", // e-mail
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)), null, // createdDate
				null, // lastModifiedBy
				null // lastModifiedDate
		);

		restMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
												.content(TestUtil.convertObjectToJsonBytes(validUser)))
				.andExpect(status().isCreated());

		Optional<User> user = userRepository.findOneByLogin("joe");
		assertThat(user.isPresent()).isTrue();
	}

	@Test
	public void testRegisterInvalidLogin() throws Exception {
		ManagedUserDto invalidUser = new ManagedUserDto(null, // id
				"funky-log!n", // login <-- invalid
				"password", // password
				"Funky", // firstName
				"One", // lastName
				"funky@example.com", // e-mail
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)), null, // createdDate
				null, // lastModifiedBy
				null // lastModifiedDate
		);

		restUserMockMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
														.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
						.andExpect(status().isBadRequest());

		Optional<User> user = userRepository.findOneByEmail("funky@example.com");
		assertThat(user.isPresent()).isFalse();
	}

	@Test
	public void testRegisterInvalidEmail() throws Exception {
		ManagedUserDto invalidUser = new ManagedUserDto(null, // id
				"bob", // login
				"password", // password
				"Bob", // firstName
				"Green", // lastName
				"invalid", // e-mail <-- invalid
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)), null, // createdDate
				null, // lastModifiedBy
				null // lastModifiedDate
		);

		restUserMockMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
														.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
						.andExpect(status().isBadRequest());

		Optional<User> user = userRepository.findOneByLogin("bob");
		assertThat(user.isPresent()).isFalse();
	}

	@Test
	public void testRegisterInvalidPassword() throws Exception {
		ManagedUserDto invalidUser = new ManagedUserDto(null, // id
				"bob", // login
				"123", // password with only 3 digits
				"Bob", // firstName
				"Green", // lastName
				"bob@example.com", // e-mail
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)), null, // createdDate
				null, // lastModifiedBy
				null // lastModifiedDate
		);

		restUserMockMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
														.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
						.andExpect(status().isBadRequest());

		Optional<User> user = userRepository.findOneByLogin("bob");
		assertThat(user.isPresent()).isFalse();
	}

	@Test
	public void testRegisterDuplicateLogin() throws Exception {
		// Good
		ManagedUserDto validUser = new ManagedUserDto(null, // id
				"alice", // login
				"password", // password
				"Alice", // firstName
				"Something", // lastName
				"alice@example.com", // e-mail
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)), null, // createdDate
				null, // lastModifiedBy
				null // lastModifiedDate
		);

		// Duplicate login, different e-mail
		ManagedUserDto duplicatedUser = new ManagedUserDto(validUser.getId(), validUser.getLogin(),
				validUser.getPassword(), validUser.getLogin(), validUser.getLastName(), "alicejr@example.com", true,
				validUser.getLangKey(), validUser.getAuthorities(), validUser.getCreatedDate(),
				validUser.getLastModifiedBy(), validUser.getLastModifiedDate());

		// Good user
		restMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
												.content(TestUtil.convertObjectToJsonBytes(validUser)))
				.andExpect(status().isCreated());

		// Duplicate login
		restMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
												.content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
				.andExpect(status().is4xxClientError());

		Optional<User> userDup = userRepository.findOneByEmail("alicejr@example.com");
		assertThat(userDup.isPresent()).isFalse();
	}

	@Test
	public void testRegisterDuplicateEmail() throws Exception {
		// Good
		ManagedUserDto validUser = new ManagedUserDto(null, // id
				"john", // login
				"password", // password
				"John", // firstName
				"Doe", // lastName
				"john@example.com", // e-mail
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)), null, // createdDate
				null, // lastModifiedBy
				null // lastModifiedDate
		);

		// Duplicate e-mail, different login
		ManagedUserDto duplicatedUser = new ManagedUserDto(validUser.getId(), "johnjr", validUser.getPassword(),
				validUser.getLogin(), validUser.getLastName(), validUser.getEmail(), true, validUser.getLangKey(),
				validUser.getAuthorities(), validUser.getCreatedDate(), validUser.getLastModifiedBy(),
				validUser.getLastModifiedDate());

		// Good user
		restMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
												.content(TestUtil.convertObjectToJsonBytes(validUser)))
				.andExpect(status().isCreated());

		// Duplicate e-mail
		restMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
												.content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
				.andExpect(status().is4xxClientError());

		Optional<User> userDup = userRepository.findOneByLogin("johnjr");
		assertThat(userDup.isPresent()).isFalse();
	}

	@Test
	public void testRegisterAdminIsIgnored() throws Exception {
		ManagedUserDto validUser = new ManagedUserDto(null, // id
				"badguy", // login
				"password", // password
				"Bad", // firstName
				"Guy", // lastName
				"badguy@example.com", // e-mail
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.ADMIN)), null, // createdDate
				null, // lastModifiedBy
				null // lastModifiedDate
		);

		restMvc	.perform(post("/api/register")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
												.content(TestUtil.convertObjectToJsonBytes(validUser)))
				.andExpect(status().isCreated());

		Optional<User> userDup = userRepository.findOneByLogin("badguy");
		assertThat(userDup.isPresent()).isTrue();
		assertThat(userDup	.get()
							.getAuthorities())	.hasSize(1)
												.containsExactly(
														authorityRepository.findOne(AuthoritiesConstants.USER));
	}

	@Test
	public void testSaveInvalidLogin() throws Exception {
		UserDto invalidUser = new UserDto("funky-log!n", // login <-- invalid
				"Funky", // firstName
				"One", // lastName
				"funky@example.com", // e-mail
				true, // activated
				"en", // langKey
				new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)));

		restUserMockMvc	.perform(post("/api/account")	.contentType(TestUtil.APPLICATION_JSON_UTF8)
														.content(TestUtil.convertObjectToJsonBytes(invalidUser)))
						.andExpect(status().isBadRequest());

		Optional<User> user = userRepository.findOneByEmail("funky@example.com");
		assertThat(user.isPresent()).isFalse();
	}
}
