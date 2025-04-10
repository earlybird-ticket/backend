package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.dto.request.VenueUpdatePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueUpdateEventHandler implements EventHandler<VenueUpdatePayload> {
    @Override
    public void handle(Event<VenueUpdatePayload> event) {
        //consumer 로직 작성
        //venueService.update();
    }

    @Override
    public boolean support(Event<VenueUpdatePayload> event) {
        return false;
    }
}
