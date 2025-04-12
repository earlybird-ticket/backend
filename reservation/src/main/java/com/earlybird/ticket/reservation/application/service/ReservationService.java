package com.earlybird.ticket.reservation.application.service;

import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;

import java.util.List;
import java.util.UUID;

public interface ReservationService {
    List<String> createResrvation(List<CreateReservationCommand> createReservationCommand,
                                  String passport);

    void cancelReservation(UUID reservationId,
                           String passport);

    FindReservationQuery findReservation(UUID reservationId,
                                         String passport);
}
