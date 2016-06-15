package io.thingcare.modules.command;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.messaging.MessageDispatchInterceptor;

public class CustomQueryGateway extends DefaultCommandGateway implements QueryGateway{

	@SafeVarargs
	public CustomQueryGateway(CommandBus commandBus,
			MessageDispatchInterceptor<CommandMessage<?>>... messageDispatchInterceptors) {
		super(commandBus, messageDispatchInterceptors);
	}
}
