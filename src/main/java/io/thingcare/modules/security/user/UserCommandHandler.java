package io.thingcare.modules.security.user;

import io.thingcare.api.security.user.CreateUserCommand;
import io.thingcare.api.security.user.ManagedUserDto;
import io.thingcare.modules.security.user.exception.UserEmailAlreadyExistsException;
import io.thingcare.modules.security.user.exception.UserLoginAlreadyExistsException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class UserCommandHandler {

	@Autowired
	private UserService userService;

	@Autowired
	private UserFactory userFactory;

	@CommandHandler
	public User createUser(CreateUserCommand command) {
		ManagedUserDto userDto = command.getManagedUserDto();
		validate(userDto);
		User user = userFactory.create(userDto);
		return userService.save(user);
	}

	private void validate(ManagedUserDto userDto) {
		validateLogin(userDto);
		validateEmail(userDto);
	}

	private void validateLogin(ManagedUserDto userDto) {
		boolean userLoginExists = userService.userLoginExists(userDto.getLogin());
		if(userLoginExists) {
			throw new UserLoginAlreadyExistsException("User login already in use");
		}
	}

	private void validateEmail(ManagedUserDto userDto) {
		boolean userEmailExists = userService.userEmailExists(userDto.getEmail());
		if(userEmailExists) {
			throw new UserEmailAlreadyExistsException("E-mail address already in use");
		}
	}
}
