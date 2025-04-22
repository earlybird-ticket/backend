package com.earlybird.ticket.alarm.application.dispatcher;

import com.earlybird.ticket.alarm.domain.Event;
import com.earlybird.ticket.common.entity.EventPayload;

public interface EventDispatcher {
    void handle(Event<? extends EventPayload> event);
}
