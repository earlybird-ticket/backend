package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.application.event.dto.request.VenueDeletePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueDeleteEventHandler implements EventHandler<VenueDeletePayload> {

    private final VenueHandlerService venueHandlerService;

    @Override
    public void handle(Event<VenueDeletePayload> event) {
        VenueDeletePayload payload = event.getPayload();
        venueHandlerService.delete(payload);
    }

    @Override
    public boolean support(Event<VenueDeletePayload> event) {
        return event.getEventType().equals(EventType.VENUE_DELETE);
    }
}
