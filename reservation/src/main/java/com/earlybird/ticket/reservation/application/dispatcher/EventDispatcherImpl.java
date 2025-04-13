package com.earlybird.ticket.reservation.application.dispatcher;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDispatcherImpl implements EventDispatcher {

    private final List<EventHandler<? extends EventPayload>> handlerList;

    @Override
    public void handle(Event<? extends EventPayload> event) {
        handlerList.stream()
                   .filter(handler -> ((EventHandler) handler).support(event))
                   .findFirst()
                   .ifPresentOrElse(handler -> ((EventHandler) handler).handle(event),
                                    () -> log.error("No handler found for event: {}",
                                                    event.getEventType()));
    }
}
