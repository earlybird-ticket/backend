package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.SeatCreatePayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatCreateEventHandler implements EventHandler<SeatCreatePayload> {

    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<SeatCreatePayload> event) {
        SeatCreatePayload payload = event.getPayload();
        seatHandlerService.createSeat(payload);
    }

    @Override
    public boolean support(Event<SeatCreatePayload> event) {
        return event.getEventType().equals(EventType.SEAT_CREATE);
    }
}
