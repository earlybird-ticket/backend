package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.SeatConfirmPayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatConfirmEventHandler implements EventHandler<SeatConfirmPayload> {

    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<SeatConfirmPayload> event) {
        SeatConfirmPayload payload = event.getPayload();
        seatHandlerService.confirmSeat(payload);
    }

    @Override
    public boolean support(Event<SeatConfirmPayload> event) {
        return event.getEventType().equals(EventType.SEAT_CONFIRM);
    }
}
