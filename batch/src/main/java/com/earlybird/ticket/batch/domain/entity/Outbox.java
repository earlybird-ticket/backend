package com.earlybird.ticket.batch.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("success is false")
@SequenceGenerator(
    name = "OUTBOX_SEQ_GENERATOR",
    sequenceName = "P_OUTBOX_SEQ",
    initialValue = 1, allocationSize = 100
)
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "OUTBOX_SEQ_GENERATOR")
    private Long id;

    @Column(name = "org_id")
    private Long orgId; // 각 서비스에서의 ID

    private String aggregateType;

    private UUID aggregateId;           // 예: 주문 ID

    private String eventType;          // 이벤트 타입

    @Column(columnDefinition = "TEXT")
    private String payload;               // 직렬화된 JSON 형태

    private int retryCount;               // 재시도 횟수

    private boolean success;              // 발행 성공 여부

    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    @Builder(builderMethodName = "withoutId")
    public Outbox(
        Long orgId,
        String aggregateType,
        UUID aggregateId,
        String eventType,
        String payload,
        int retryCount,
        boolean success,
        LocalDateTime createdAt,
        LocalDateTime sentAt
    ) {
        this.orgId = orgId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.retryCount = retryCount;
        this.success = success;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }
}
