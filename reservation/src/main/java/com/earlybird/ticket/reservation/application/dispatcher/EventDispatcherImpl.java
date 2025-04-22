package com.earlybird.ticket.reservation.application.dispatcher;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDispatcherImpl implements EventDispatcher {

    private final List<EventHandler<? extends EventPayload>> handlerList;

    @SuppressWarnings("unchecked")
    @Override
    public <T extends EventPayload> void handle(Event<T> event) {
        handlerList.stream()
                   .filter(handler -> supports(handler,
                                               event))
                   .map(handler -> (EventHandler<T>) handler)
                   .findFirst()
                   .ifPresentOrElse(handler -> handler.handle(event),
                                    () -> log.error("No handler found for event type: {}",
                                                    event.getEventType()));
    }

    @SuppressWarnings("unchecked")
    private <T extends EventPayload> boolean supports(EventHandler<?> handler,
                                                      Event<T> event) {
        return ((EventHandler<T>) handler).support(event);
    }
}