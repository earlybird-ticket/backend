package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.SeatInstanceCreatePayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatInstanceCreateEventHandler implements EventHandler<SeatInstanceCreatePayload> {

    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<SeatInstanceCreatePayload> event) {
        SeatInstanceCreatePayload payload = event.getPayload();
        seatHandlerService.createSeatInstance(payload);
    }

    @Override
    public boolean support(Event<SeatInstanceCreatePayload> event) {
        return event.getEventType().equals(EventType.SEAT_INSTANCE_CREATE);
    }
}
