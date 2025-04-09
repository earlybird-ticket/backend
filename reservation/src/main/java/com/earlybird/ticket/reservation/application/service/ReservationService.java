package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.presentation.dto.response.FindReservationQuery;

public interface ReservationService {
    void createResrvation(CreateReservationCommand createReservationCommand);

    void cancelReservation(String reservationId,
                           String passport);

    FindReservationQuery findReservation(String reservationId,
                                         String passport);
}
