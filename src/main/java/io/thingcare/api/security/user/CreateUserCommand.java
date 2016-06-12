package io.thingcare.api.security.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserCommand {

    private ManagedUserDto managedUserDto;
}
