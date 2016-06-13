package io.thingcare.security.user;

import io.thingcare.api.security.user.CreateUserCommand;
import io.thingcare.api.security.user.ManagedUserDto;
import io.thingcare.modules.security.user.UserCommandHandler;
import io.thingcare.modules.security.user.UserFactory;
import io.thingcare.modules.security.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserCommandHandlerTest {

	@InjectMocks
	private UserCommandHandler handler = new UserCommandHandler();

	@Mock
	private CreateUserCommand command;
	@Mock
	private UserFactory factory;
	@Mock
	private UserService service;

	@Test
	public void createUserCommandHandlingTest() {
		givenCommandWithCreateUser();
		whenHandlingCommand();
		thenFactoryIsInvoked();
	}

	private void thenFactoryIsInvoked() {
		Mockito.verify(factory).create(Mockito.any(ManagedUserDto.class));
	}

	private void whenHandlingCommand() {
		handler.createUser(command);
	}

	private void givenCommandWithCreateUser() {
		Mockito.when(command.getManagedUserDto()).thenReturn(ManagedUserDto.builder().build());
	}

}
