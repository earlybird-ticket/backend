package com.earlybird.ticket.payment.application.event.dispatcher;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.payment.application.event.handler.EventHandler;
import com.earlybird.ticket.payment.domain.entity.Event;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDispatcherImpl implements EventDispatcher {

    private final List<EventHandler<? extends EventPayload>> handlers;


    @Override
    public void handle(Event<? extends EventPayload> event) {
        handlers.stream()
            .filter(handler -> ((EventHandler) handler).supports(event))
            .findFirst()
            .ifPresentOrElse(
                handler -> ((EventHandler) handler).handle(event),
                () -> log.error("No handler found for event: {}", event.getEventType())
            );
    }
}
