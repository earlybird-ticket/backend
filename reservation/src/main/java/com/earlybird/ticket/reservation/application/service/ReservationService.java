package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.domain.dto.response.ReservationSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReservationService {

    void cancelReservation(UUID reservationId,
                           String passport);

    FindReservationQuery findReservation(UUID reservationId,
                                         String passport);

    Page<ReservationSearchResult> searchReservations(Pageable pageable,
                                                     String q,
                                                     String startTime,
                                                     String endTime,
                                                     String passport);

    void test();
}
