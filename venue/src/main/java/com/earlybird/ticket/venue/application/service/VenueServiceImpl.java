package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.event.dto.request.VenueCreatePayload;
import com.earlybird.ticket.venue.application.event.dto.request.VenueDeletePayload;
import com.earlybird.ticket.venue.application.event.dto.request.VenueUpdatePayload;
import com.earlybird.ticket.venue.domain.entity.Venue;
import com.earlybird.ticket.venue.domain.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

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
                    venueCreatePayload.hallList()
        );
        //3. venue 저장
        venueRepository.save(venue);
    }

    @Override
    public void update(VenueUpdatePayload venueUpdatePayload) {
        //0. passport에서 userId 가져오기
        //1. venue 가져오기
        //2. venue update
    }

    @Override
    public void delete(VenueDeletePayload venueDeletePayload) {
        //0. passport에서 userId 가져오기
        //1. 공연장 + 홀 가져오기
        //2. 좌석 + 좌석 인스턴스 가져오기
        //3. 모두 delete update
    }
}
