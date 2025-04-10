package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.Event;
import com.earlybird.ticket.venue.application.event.dto.request.VenueDeletePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueDeleteEventHandler implements EventHandler<VenueDeletePayload> {
    @Override
    public void handle(Event<VenueDeletePayload> event) {
        //consumer 로직 작성
        //venueService.delete();
    }

    @Override
    public boolean support(Event<VenueDeletePayload> event) {
        return false;
    }
}
