package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatCreatePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatCreateEventHandler implements EventHandler<SeatCreatePayload> {
    @Override
    public void handle(Event<SeatCreatePayload> event) {
        //consumer 로직 작성
        //seatService.createSeat();
    }

    @Override
    public boolean support(Event<SeatCreatePayload> event) {
        return false;
    }
}
