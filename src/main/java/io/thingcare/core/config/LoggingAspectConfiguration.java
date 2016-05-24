package io.thingcare.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import io.thingcare.core.logging.LoggingAspect;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

	@Bean
	@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
	public LoggingAspect loggingAspect() {
		return new LoggingAspect();
	}
}
