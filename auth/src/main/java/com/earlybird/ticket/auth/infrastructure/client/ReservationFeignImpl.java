package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.auth.application.ReservationClient;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationFeignImpl implements ReservationClient {
    private final ReservationFeignClient reservationFeignClient;

    @Override
    public Boolean isExistUserReservation(PassportDto passportDto) {
        return reservationFeignClient.isExist(passportDto)
                                     .getData();
    }

}
