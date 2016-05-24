package io.thingcare.modules.security.user;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import io.thingcare.api.security.user.UserDto;
import io.thingcare.modules.security.authority.Authority;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {

	UserDto asDto(User user);

	default Set<String> stringsFromAuthorities(Set<Authority> authorities) {
		return authorities	.stream()
							.map(Authority::getName)
							.collect(Collectors.toSet());
	}

}
