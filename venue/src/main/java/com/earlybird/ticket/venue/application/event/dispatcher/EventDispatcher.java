package com.earlybird.ticket.venue.application.event.dispatcher;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.application.event.Event;

public interface EventDispatcher {
    void handle(Event<? extends EventPayload> event);
}
