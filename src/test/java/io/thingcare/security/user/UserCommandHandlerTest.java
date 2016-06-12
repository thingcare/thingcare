package io.thingcare.security.user;

import io.thingcare.api.security.user.CreateUserCommand;
import io.thingcare.api.security.user.ManagedUserDto;
import io.thingcare.modules.security.user.UserCommandHandler;
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
	private UserService service = new UserService();

	@Test
	public void createUserCommandHandlingTest() {
		givenCommandWithCreateUser();
		whenHandlingCommand();
		thenServiceIsInvoked();
	}

	private void thenServiceIsInvoked() {
		Mockito.verify(service).createUser(Mockito.any(ManagedUserDto.class));
	}

	private void whenHandlingCommand() {
		handler.createUser(command);
	}

	private void givenCommandWithCreateUser() {
		Mockito.when(command.getManagedUserDto()).thenReturn(ManagedUserDto.builder().build());
	}

}
