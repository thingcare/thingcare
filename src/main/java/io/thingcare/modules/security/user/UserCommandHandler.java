package io.thingcare.modules.security.user;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class UserCommandHandler {

	@Autowired
	private UserService userService;

	@CommandHandler
	public User createUser(CreateUserCommand command) {
		return userService.createUser(command.getManagedUserDto());
	}
}
