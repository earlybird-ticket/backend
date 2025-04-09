package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatPreemptPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatPreemptEventHandler implements EventHandler<SeatPreemptPayload> {
    @Override
    public void handle(Event<SeatPreemptPayload> event) {
        //consumer 로직 작성
        //seatService.preemptSeatInstance();
    }

    @Override
    public boolean support(Event<SeatPreemptPayload> event) {
        return false;
    }
}
