package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatInstanceCreatePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatInstanceCreateEventHandler implements EventHandler<SeatInstanceCreatePayload> {
    @Override
    public void handle(Event<SeatInstanceCreatePayload> event) {
        //consumer 로직 작성
        //seatService.createSeatInstance();
    }

    @Override
    public boolean support(Event<SeatInstanceCreatePayload> event) {
        return false;
    }
}
