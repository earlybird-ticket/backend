package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.VenueCreatePayload;
import com.earlybird.ticket.venue.application.event.dto.request.VenueDeletePayload;
import com.earlybird.ticket.venue.application.event.dto.request.VenueUpdatePayload;
import com.earlybird.ticket.venue.common.exception.VenueNotFoundException;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.Venue;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import com.earlybird.ticket.venue.domain.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueHandlerServiceImpl implements VenueHandlerService {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public void create(VenueCreatePayload venueCreatePayload) {
        //1. venue create
        //2. hall create
        Venue venue = Venue.create(
                    venueCreatePayload.venueName(),
                    venueCreatePayload.location(),
                    venueCreatePayload.area(),
                    venueCreatePayload.totalNumberOfSeat(),
                    venueCreatePayload.passportDto().getUserId(),
                    venueCreatePayload.toHallCreateResult()
        );
        //3. venue 저장
        venueRepository.save(venue);
    }

    @Override
    @Transactional
    public void update(VenueUpdatePayload venueUpdatePayload) {
        //1. venue 가져오기
        Venue venue = venueRepository.findById(venueUpdatePayload.venueId());

        if(venue == null) {
            throw new VenueNotFoundException();
        }

        //2. venue update
        venue.updateVenue(
                venueUpdatePayload.venueName(),
                venueUpdatePayload.location(),
                venueUpdatePayload.area(),
                venueUpdatePayload.totalNumberOfSeat(),
                venueUpdatePayload.passportDto().getUserId()
        );
    }

    @Override
    @Transactional
    public void delete(VenueDeletePayload venueDeletePayload) {
        //0. passport에서 userId 가져오기
        //1. 공연장 + 홀 가져오기
        Venue venue = venueRepository.findVenueWithHallById(venueDeletePayload.venueId());
        //2. 좌석 + 좌석 인스턴스 가져오기
        List<Seat> seatList = seatRepository.findSeatListWithSeatInstanceByVenueId(venueDeletePayload.venueId());
        //3. 모두 delete update
        venue.deleteVenueAndHall(venueDeletePayload.passportDto().getUserId());

        for(Seat seat : seatList) {
            seat.deleteSeatAndSeatInstance(venueDeletePayload.passportDto().getUserId());
        }
    }
}
