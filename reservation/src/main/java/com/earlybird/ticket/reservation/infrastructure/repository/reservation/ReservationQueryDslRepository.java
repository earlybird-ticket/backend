package com.earlybird.ticket.reservation.infrastructure.repository.reservation;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.domain.dto.response.ReservationSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationQueryDslRepository {
    Page<ReservationSearchResult> searchReservations(String q,
                                                     String startTime,
                                                     String endTime,
                                                     Pageable pageable,
                                                     PassportDto passportDto);
}
