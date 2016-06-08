package io.thingcare.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.mongobee.Mongobee;
import com.mongodb.Mongo;

import io.thingcare.core.config.security.OAuth2AuthenticationReadConverter;
import io.thingcare.core.config.utils.JSR310DateConverters.DateToLocalDateConverter;
import io.thingcare.core.config.utils.JSR310DateConverters.DateToLocalDateTimeConverter;
import io.thingcare.core.config.utils.JSR310DateConverters.DateToZonedDateTimeConverter;
import io.thingcare.core.config.utils.JSR310DateConverters.LocalDateTimeToDateConverter;
import io.thingcare.core.config.utils.JSR310DateConverters.LocalDateToDateConverter;
import io.thingcare.core.config.utils.JSR310DateConverters.ZonedDateTimeToDateConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableMongoRepositories("io.thingcare")
@Import(value = MongoAutoConfiguration.class)
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class DatabaseConfiguration extends AbstractMongoConfiguration {

	@Autowired
	private Mongo mongo;

	@Autowired
	private MongoProperties mongoProperties;

	@Bean
	public ValidatingMongoEventListener validatingMongoEventListener() {
		return new ValidatingMongoEventListener(validator());
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Override
	protected String getDatabaseName() {
		return mongoProperties.getDatabase();
	}

	@Override
	public Mongo mongo() throws Exception {
		return mongo;
	}

	@Bean
	public CustomConversions customConversions() {
		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(new OAuth2AuthenticationReadConverter());
		converters.add(DateToZonedDateTimeConverter.INSTANCE);
		converters.add(ZonedDateTimeToDateConverter.INSTANCE);
		converters.add(DateToLocalDateConverter.INSTANCE);
		converters.add(LocalDateToDateConverter.INSTANCE);
		converters.add(DateToLocalDateTimeConverter.INSTANCE);
		converters.add(LocalDateTimeToDateConverter.INSTANCE);
		return new CustomConversions(converters);
	}

	@Bean
	public Mongobee mongobee() {
		log.debug("Configuring Mongobee");
		Mongobee mongobee = new Mongobee(mongo);
		mongobee.setDbName(mongoProperties.getDatabase());
		// package to scan for migrations
		mongobee.setChangeLogsScanPackage("io.thingcare");
		mongobee.setEnabled(true);
		return mongobee;
	}
}
