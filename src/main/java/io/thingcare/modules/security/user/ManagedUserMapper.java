package io.thingcare.modules.security.user;

import org.mapstruct.Mapper;

import io.thingcare.api.security.user.ManagedUserDto;

@Mapper(componentModel = "spring", uses = {})
public interface ManagedUserMapper {

	ManagedUserDto asDto(User user);

}
