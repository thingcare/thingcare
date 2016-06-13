package io.thingcare.modules.security.user;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import io.thingcare.api.security.user.ManagedUserDto;
import io.thingcare.api.util.RandomUtil;
import io.thingcare.modules.security.authority.Authority;
import io.thingcare.modules.security.authority.AuthorityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFactory {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthorityRepository authorityRepository;

	public User create(ManagedUserDto managedUserDto) {
		User user = new User();
		user.setLogin(managedUserDto.getLogin());
		user.setFirstName(managedUserDto.getFirstName());
		user.setLastName(managedUserDto.getLastName());
		user.setEmail(managedUserDto.getEmail());
		if (managedUserDto.getLangKey() == null) {
			user.setLangKey("en");
		} else {
			user.setLangKey(managedUserDto.getLangKey());
		}
		if (managedUserDto.getAuthorities() != null) {
			Set<Authority> authorities = new HashSet<>();
			managedUserDto.getAuthorities().stream()
					.forEach(authority -> authorities.add(authorityRepository.findOne(authority)));
			user.setAuthorities(authorities);
		}
		String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtil.generateResetKey());
		user.setResetDate(ZonedDateTime.now());
		user.setActivated(true);
		log.debug("Created Information for User: {}", user);
		return user;
	}

}
