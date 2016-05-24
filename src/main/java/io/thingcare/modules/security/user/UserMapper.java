package io.thingcare.modules.security.user;

import org.mapstruct.Mapper;

import io.thingcare.api.security.user.UserDto;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {

	UserDto asDto(User user);

}
