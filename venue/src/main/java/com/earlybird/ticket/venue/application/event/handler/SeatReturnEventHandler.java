package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatReturnPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatReturnEventHandler implements EventHandler<SeatReturnPayload> {
    @Override
    public void handle(Event<SeatReturnPayload> event) {
        //consumer 로직 작성
        //seatService.returnSeatInstance();
    }

    @Override
    public boolean support(Event<SeatReturnPayload> event) {
        return false;
    }
}
