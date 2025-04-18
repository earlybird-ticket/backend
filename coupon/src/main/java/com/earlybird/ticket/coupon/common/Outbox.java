package com.earlybird.ticket.coupon.common;

import com.earlybird.ticket.coupon.application.event.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

// Outbox Entity 정의
@Entity
@Table(name = "p_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("success is false")
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private int retryCount;

    private boolean success;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    @Builder
    public Outbox(
            String aggregateType, UUID aggregateId, EventType eventType, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.retryCount = 0;
        this.success = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markSuccess() {
        this.success = true;
        this.sentAt = LocalDateTime.now();
    }

    public void incrementRetry() {
        this.retryCount++;
    }

    public static class AggregateType {

        public static final String RESERVATION = "RESERVATION";
    }
}