package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.dto.request.VenueCreatePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueUpdateEventHandler implements EventHandler<VenueCreatePayload> {
    @Override
    public void handle(Event<VenueCreatePayload> event) {
        //consumer 로직 작성
        //venueService.update();
    }

    @Override
    public boolean support(Event<VenueCreatePayload> event) {
        return false;
    }
}
