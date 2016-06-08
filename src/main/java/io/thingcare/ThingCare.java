package io.thingcare;

import io.thingcare.core.config.ThingcareProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ ThingcareProperties.class })
public class ThingCare {

	public static void main(String[] args) {
		SpringApplication.run(ThingCare.class, args);
	}
}
