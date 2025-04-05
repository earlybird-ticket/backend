package com.earlybird.ticket.infrastructure.messaging;

import com.earlybird.ticket.application.service.PaymentEventProducer;
import com.earlybird.ticket.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentKafkaEventProducer implements PaymentEventProducer {

    private final PaymentService paymentService;
}
