package com.earlybird.ticket.reservation.application.dispatcher;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.domain.entity.Event;

public interface EventDispatcher {
    void handle(Event<? extends EventPayload> event);
}
