package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;

import java.util.List;

public interface ReservationService {
    void createResrvation(List<CreateReservationCommand> createReservationCommand,
                          String passport);

    void cancelReservation(String reservationId,
                           String passport);

    FindReservationQuery findReservation(String reservationId,
                                         String passport);
}
