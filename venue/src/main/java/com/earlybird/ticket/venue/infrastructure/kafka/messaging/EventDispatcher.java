package com.earlybird.ticket.venue.infrastructure.kafka.messaging;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.domain.entity.Event;

public interface EventDispatcher {
    void handle(Event<? extends EventPayload> event);
}
