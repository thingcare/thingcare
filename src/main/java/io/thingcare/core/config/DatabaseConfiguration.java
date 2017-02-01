package io.thingcare.core.config;

import com.mongodb.Mongo;
import io.thingcare.core.config.utils.JSR310DateConverters.*;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;

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
		converters.add(DateToZonedDateTimeConverter.INSTANCE);
		converters.add(ZonedDateTimeToDateConverter.INSTANCE);
		converters.add(DateToLocalDateConverter.INSTANCE);
		converters.add(LocalDateToDateConverter.INSTANCE);
		converters.add(DateToLocalDateTimeConverter.INSTANCE);
		converters.add(LocalDateTimeToDateConverter.INSTANCE);
		return new CustomConversions(converters);
	}
}
