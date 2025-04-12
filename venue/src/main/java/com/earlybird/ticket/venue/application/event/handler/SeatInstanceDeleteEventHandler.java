package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.SeatInstanceDeletePayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatInstanceDeleteEventHandler implements EventHandler<SeatInstanceDeletePayload> {

    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<SeatInstanceDeletePayload> event) {
        SeatInstanceDeletePayload payload = event.getPayload();
        seatHandlerService.deleteSeatInstance(payload);
    }

    @Override
    public boolean support(Event<SeatInstanceDeletePayload> event) {
        return event.getEventType().equals(EventType.SEAT_INSTANCE_DELETE);
    }
}
