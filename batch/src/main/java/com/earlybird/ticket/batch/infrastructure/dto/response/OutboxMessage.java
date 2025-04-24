package com.earlybird.ticket.batch.infrastructure.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OutboxMessage(
    Long id,
    String aggregateType,
    UUID aggregateId,
    String eventType,
    String payload,
    int retryCount,
    boolean success,
    LocalDateTime createdAt,
    LocalDateTime sentAt

) {


}