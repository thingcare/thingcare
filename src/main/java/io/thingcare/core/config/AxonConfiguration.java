package io.thingcare.core.config;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@ConditionalOnClass(value = CommandBus.class)
public class AxonConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public CommandBus commandBus() {
		SimpleCommandBus commandBus = new SimpleCommandBus();
		commandBus.setHandlerInterceptors(Collections.singletonList(new BeanValidationInterceptor<>()));
		return commandBus;
	}

	@Bean
	@ConditionalOnMissingBean
	public CommandBus queryBus() {
		SimpleCommandBus queryBus = new SimpleCommandBus();
		queryBus.setHandlerInterceptors(Collections.singletonList(new BeanValidationInterceptor<>()));
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
		return new DefaultCommandGateway(queryBus());
	}

}
