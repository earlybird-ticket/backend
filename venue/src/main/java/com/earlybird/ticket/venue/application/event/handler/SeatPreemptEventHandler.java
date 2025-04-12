package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.SeatPreemptPayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatPreemptEventHandler implements EventHandler<SeatPreemptPayload> {
    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<SeatPreemptPayload> event) {
        SeatPreemptPayload payload = event.getPayload();
        seatHandlerService.preemptSeat(payload);
    }

    @Override
    public boolean support(Event<SeatPreemptPayload> event) {
        return event.getEventType().equals(EventType.SEAT_PREEMPT);
    }
}
