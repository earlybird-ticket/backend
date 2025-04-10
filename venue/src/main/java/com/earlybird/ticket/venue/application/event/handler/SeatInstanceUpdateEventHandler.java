package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatInstanceUpdatePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatInstanceUpdateEventHandler implements EventHandler<SeatInstanceUpdatePayload> {
    @Override
    public void handle(Event<SeatInstanceUpdatePayload> event) {
        //consumer 로직 작성
        //seatService.updateSeatInstance();
    }

    @Override
    public boolean support(Event<SeatInstanceUpdatePayload> event) {
        return false;
    }
}
