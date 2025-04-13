package com.earlybird.ticket.reservation.application.event;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.domain.entity.Event;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);

    boolean support(Event<T> event);
}
