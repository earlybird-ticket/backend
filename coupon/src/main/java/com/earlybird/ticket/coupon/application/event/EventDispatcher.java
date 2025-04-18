package com.earlybird.ticket.coupon.application.event;

import com.earlybird.ticket.common.entity.EventPayload;

public interface EventDispatcher {

    void handle(Event<? extends EventPayload> event);
}
