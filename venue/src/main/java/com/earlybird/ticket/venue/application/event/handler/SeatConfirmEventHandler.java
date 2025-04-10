package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.application.event.dto.request.SeatConfirmPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatConfirmEventHandler implements EventHandler<SeatConfirmPayload> {
    @Override
    public void handle(Event<SeatConfirmPayload> event) {
        //consumer 로직 작성
        //seatService.confirmSeatInstance();
    }

    @Override
    public boolean support(Event<SeatConfirmPayload> event) {
        return false;
    }
}
