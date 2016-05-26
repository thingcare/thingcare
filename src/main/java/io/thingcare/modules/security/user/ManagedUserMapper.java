package io.thingcare.modules.security.user;

import org.mapstruct.Mapper;

import io.thingcare.api.security.user.ManagedUserDto;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ManagedUserMapper {

	ManagedUserDto asDto(User user);

}
