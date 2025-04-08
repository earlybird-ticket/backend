package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.common.entity.PassportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "reservation-service")
public interface ReservationFeignClient {

    @GetMapping("/api/v1/internal/reservations")
    CommonDto<Boolean> isExist(PassportDto passportDto);
}
