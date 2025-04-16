package com.earlybird.ticket.reservation.application.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.reservation.domain.entity.constant.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentSuccessPayload(UUID paymentId,
                                    UUID reservationId,
                                    PaymentMethod paymentMethod,
                                    BigDecimal totalPrice,
                                    PaymentStatus paymentStatus,
                                    PassportDto passportDto) implements EventPayload {

}