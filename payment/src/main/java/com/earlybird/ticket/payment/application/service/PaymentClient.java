package com.earlybird.ticket.payment.application.service;

import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCancelCommand;
import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCommand;
import java.util.UUID;

public interface PaymentClient {

    UpdatePaymentCommand confirmPayment(ConfirmPaymentCommand command, UUID paymentId);

    UpdatePaymentCancelCommand cancelPayment(String paymentKey, UUID reservationId);
}
