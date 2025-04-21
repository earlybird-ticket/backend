package com.earlybird.ticket.payment.application.event.dispatcher;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.payment.domain.entity.Event;

public interface EventDispatcher {

    void handle(Event<? extends EventPayload> event);
}
