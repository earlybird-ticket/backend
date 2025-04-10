package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.domain.entity.Event;
import com.earlybird.ticket.venue.domain.entity.constant.EventType;
import com.earlybird.ticket.venue.application.event.dto.request.VenueCreatePayload;
import com.earlybird.ticket.venue.application.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VenueCreateEventHandler implements EventHandler<VenueCreatePayload> {

    private final VenueService venueService;

    @Override
    public void handle(Event<VenueCreatePayload> event) {
        //VenueCreatePayload payload = event.getPayload();
        //venueService.create(payload);
    }

    @Override
    public boolean support(Event<VenueCreatePayload> event) {
        return event.getEventType().equals(EventType.VENUE_CREATE);
    }
}
