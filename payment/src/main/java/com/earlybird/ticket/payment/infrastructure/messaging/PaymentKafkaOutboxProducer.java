package com.earlybird.ticket.payment.infrastructure.messaging;

import com.earlybird.ticket.payment.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentKafkaOutboxProducer {

    private final OutboxRepository outboxRepository;


}
