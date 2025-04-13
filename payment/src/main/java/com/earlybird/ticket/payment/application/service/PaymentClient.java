package com.earlybird.ticket.payment.application.service;

import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import java.util.UUID;

public interface PaymentClient {

    void confirmPayment(ConfirmPaymentCommand command, UUID paymentId);
}
