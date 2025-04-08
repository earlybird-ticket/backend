package com.earlybird.ticket.auth.application;

import com.earlybird.ticket.common.entity.PassportDto;

public interface ReservationClient {

    Boolean isExistUserReservation(PassportDto passportDto);
}
