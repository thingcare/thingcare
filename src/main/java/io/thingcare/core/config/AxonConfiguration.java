package io.thingcare.core.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.thingcare.modules.command.CustomQueryGateway;
import io.thingcare.modules.command.QueryGateway;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass(value = CommandBus.class)
public class AxonConfiguration {

	@Bean
	public CommandBus commandBus() {
		SimpleCommandBus commandBus = new SimpleCommandBus();
		commandBus.registerHandlerInterceptor(new BeanValidationInterceptor<>());
		return commandBus;
	}

	@Bean
	public CommandBus queryBus() {
		SimpleCommandBus queryBus = new SimpleCommandBus();
		queryBus.registerHandlerInterceptor(new BeanValidationInterceptor<>());
		return queryBus;
	}

	@Bean
	@ConditionalOnMissingBean
	public EventBus eventBus() {
		return new SimpleEventBus();
	}

	@Bean
	@ConditionalOnMissingBean
	public CommandGateway commandGateway() {
		return new DefaultCommandGateway(commandBus());
	}

	@Bean
	@ConditionalOnMissingBean
	public QueryGateway queryGateway() {
		return new CustomQueryGateway(queryBus());
	}

}
