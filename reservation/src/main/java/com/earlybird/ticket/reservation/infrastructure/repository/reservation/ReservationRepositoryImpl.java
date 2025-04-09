package com.earlybird.ticket.reservation.infrastructure.repository.reservation;

import com.earlybird.ticket.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationRepository;
    private final ReservationQueryDslRepository reservationQueryDslRepository;
}
