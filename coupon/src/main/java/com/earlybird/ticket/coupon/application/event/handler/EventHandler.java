package com.earlybird.ticket.coupon.application.event.handler;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.coupon.application.event.Event;

public interface EventHandler<T extends EventPayload> {

    void handle(Event<T> event);

    boolean supports(Event<T> event);
}
