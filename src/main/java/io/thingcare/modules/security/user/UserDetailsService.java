package io.thingcare.modules.security.user;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Authenticate a user from the database.
 */

@Slf4j
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);
		String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
		Optional<User> userFromDatabase = userRepository.findOneByLogin(lowercaseLogin);
		return userFromDatabase	.map(user -> {
			if (!user.isActivated()) {
				throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
			}
			List<GrantedAuthority> grantedAuthorities = user.getAuthorities()
															.stream()
															.map(authority -> new SimpleGrantedAuthority(
																	authority.getName()))
															.collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(lowercaseLogin, user.getPassword(),
					grantedAuthorities);
		})
								.orElseThrow(() -> new UsernameNotFoundException(
										"User " + lowercaseLogin + " was not found in the " + "database"));
	}
}
