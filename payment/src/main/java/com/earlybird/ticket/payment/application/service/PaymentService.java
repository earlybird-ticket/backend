package com.earlybird.ticket.payment.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.command.CreatePaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.query.FindPaymentQuery;
import java.util.UUID;

public interface PaymentService {

    UUID createPayment(CreatePaymentCommand paymentRequest);

    FindPaymentQuery findPayment(UUID paymentId);

    void confirmPayment(ConfirmPaymentCommand confirmPaymentCommand);

    void cancelPayment(UUID paymentId, PassportDto passportDto);
}
