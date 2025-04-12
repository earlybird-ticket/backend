package com.earlybird.ticket.admin.application.event.handler;

import com.earlybird.ticket.admin.application.event.Event;
import com.earlybird.ticket.common.entity.EventPayload;

public interface EventHandler<T extends EventPayload> {

    void handle(Event<T> event);

    boolean supports(Event<T> event);
}
