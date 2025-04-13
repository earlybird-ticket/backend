package com.earlybird.ticket.payment.application.service;

import com.earlybird.ticket.payment.application.service.dto.command.CreatePaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.query.FindPaymentQuery;
import java.util.UUID;

public interface PaymentService {
    UUID createPayment(CreatePaymentCommand paymentRequest);

    FindPaymentQuery findPayment(UUID paymentId);
}
