package io.thingcare.modules.security.user;

import io.thingcare.api.security.user.ManagedUserDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserCommand {

    private ManagedUserDto managedUserDto;
}
