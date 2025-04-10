package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatInstanceDeletePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatInstanceDeleteEventHandler implements EventHandler<SeatInstanceDeletePayload> {
    @Override
    public void handle(Event<SeatInstanceDeletePayload> event) {
        //consumer 로직 작성
        //seatService.deleteSeatInstance();
    }

    @Override
    public boolean support(Event<SeatInstanceDeletePayload> event) {
        return false;
    }
}
