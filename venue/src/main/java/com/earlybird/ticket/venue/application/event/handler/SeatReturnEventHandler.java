package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.service.SeatService;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatReturnPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatReturnEventHandler implements EventHandler<SeatReturnPayload> {

    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<SeatReturnPayload> event) {
        SeatReturnPayload payload = event.getPayload();
        seatHandlerService.returnSeat(payload);
    }

    @Override
    public boolean support(Event<SeatReturnPayload> event) {
        return event.getEventType().equals(EventType.SEAT_RETURN);
    }
}
