package com.earlybird.ticket.alarm.application.event;

import com.earlybird.ticket.alarm.domain.Event;
import com.earlybird.ticket.common.entity.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);

    boolean support(Event<T> event);
}
