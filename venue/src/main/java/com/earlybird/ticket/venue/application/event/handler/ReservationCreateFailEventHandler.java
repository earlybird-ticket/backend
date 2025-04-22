package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.ReservationCreateFailPayload;
import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCreateFailEventHandler implements EventHandler<ReservationCreateFailPayload> {
    private final SeatHandlerService seatHandlerService;

    @Override
    public void handle(Event<ReservationCreateFailPayload> event) {
        ReservationCreateFailPayload payload = event.getPayload();
        //TODO : reservation create fail 실패 로직 작성
//        seatHandlerService.(payload);
    }

    @Override
    public boolean support(Event<ReservationCreateFailPayload> event) {
        return event.getEventType().equals(EventType.RESERVATION_CREATE_FAIL);
    }
}
