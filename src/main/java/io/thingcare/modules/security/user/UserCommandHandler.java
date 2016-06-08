package io.thingcare.modules.security.user;

import javax.inject.Inject;

import org.axonframework.commandhandling.annotation.CommandHandler;

public class UserCommandHandler {

    @Inject
    UserService userService;

    @CommandHandler
    public User createUser(CreateUserCommand command) {
        return userService.createUser(command.getManagedUserDto());
    }
}
