package io.thingcare.command

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class AxonTestApplication {
    @Bean
    TestHandler testHandler() {
        return new TestHandler()
    }
}