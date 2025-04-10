package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.domain.entity.Event;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean support(Event<T> event);
}
