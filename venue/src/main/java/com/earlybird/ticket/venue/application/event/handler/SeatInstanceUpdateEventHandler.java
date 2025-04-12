package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.SeatInstanceUpdatePayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatInstanceUpdateEventHandler implements EventHandler<SeatInstanceUpdatePayload> {

    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<SeatInstanceUpdatePayload> event) {
        SeatInstanceUpdatePayload payload = event.getPayload();
        seatHandlerService.updateSeatInstance(payload);
    }

    @Override
    public boolean support(Event<SeatInstanceUpdatePayload> event) {
        return event.getEventType().equals(EventType.SEAT_INSTANCE_UPDATE);
    }
}
