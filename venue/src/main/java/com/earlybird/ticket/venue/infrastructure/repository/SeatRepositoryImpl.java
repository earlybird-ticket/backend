package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.dto.SeatListResult;
import com.earlybird.ticket.venue.domain.dto.SectionListResult;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;
    private final SeatQueryRepository seatQueryRepository;

    @Override
    public SectionListResult findSectionList(UUID concertSequenceId) {
        return seatQueryRepository.findSectionList(concertSequenceId);
    }

    @Override
    public SeatListResult findSeatList(UUID concertSequenceId, Section section) {
        return seatQueryRepository.findSeatList(concertSequenceId, section);
    }

    @Override
    public List<Seat> findSeatListWithSeatInstanceInSeatInstanceIdList(List<UUID> seatInstanceIdList) {
        return seatQueryRepository.findSeatListWithSeatInstanceInSeatInstanceIdList(seatInstanceIdList);
    }

    @Override
    public List<Seat> saveAll(List<Seat> seatList) {
        return seatJpaRepository.saveAll(seatList);

    }

    @Override
    public List<Seat> findSeatByHallId(UUID hallId) {
        return seatJpaRepository.findByHallId(hallId);
    }

    @Override
    public Seat findSeatBySeatInstanceId(UUID seatInstanceId) {
        return seatQueryRepository.findSeatBySeatInstanceId(seatInstanceId);
    }

    @Override
    public List<Seat> findSeatListWithSeatInstanceByVenueId(UUID venueId) {
        return seatQueryRepository.findSeatListWithSeatInstanceByVenueId(venueId);
    }

    @Override
    public List<Seat> findSeatListWithSeatInstanceInConcertSequenceIdList(List<UUID> concertSequenceIdList) {
        return seatQueryRepository.findSeatListWithSeatInstanceInConcertSequenceIdList(concertSequenceIdList);
    }
}
