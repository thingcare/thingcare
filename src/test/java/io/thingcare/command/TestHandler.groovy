package io.thingcare.command

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.GenericEventMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestHandler {

    TestService testService;

    @Autowired
    EventBus eventBus;

    @CommandHandler
    def handleCommand(TestCommand testCommand) {
        testService.doSomethingWithCommand()
        eventBus.publish(new GenericEventMessage(new TestEvent()))
    }

    @EventHandler
    def handleEvent(TestEvent testEvent) {
        testService.doSomethingWithEvent()
    }

}