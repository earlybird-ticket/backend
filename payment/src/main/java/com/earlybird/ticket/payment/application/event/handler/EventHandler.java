package com.earlybird.ticket.payment.application.event.handler;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.payment.domain.entity.Event;

public interface EventHandler<T extends EventPayload> {

    void handle(Event<T> event);

    boolean supports(Event<T> event);
}
