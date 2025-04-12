package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.common.event.EventType;
import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.application.event.dto.request.VenueUpdatePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueUpdateEventHandler implements EventHandler<VenueUpdatePayload> {

    private final VenueHandlerService venueHandlerService;

    @Override
    public void handle(Event<VenueUpdatePayload> event) {
        VenueUpdatePayload payload = event.getPayload();
        venueHandlerService.update(payload);
    }

    @Override
    public boolean support(Event<VenueUpdatePayload> event) {
        return event.getEventType().equals(EventType.VENUE_UPDATE);
    }
}
