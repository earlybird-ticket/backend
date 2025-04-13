package com.earlybird.ticket.payment.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 컬럼명 재정의
@AttributeOverrides({
    @AttributeOverride(name = "createdAt", column = @Column(name = "requested_at")),
    @AttributeOverride(name = "createdBy", column = @Column(name = "requested_by")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "approved_at")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "approved_by")),
    @AttributeOverride(name = "deletedAt", column = @Column(name = "canceled_at")),
    @AttributeOverride(name = "deletedBy", column = @Column(name = "canceled_by"))
})
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_key", unique = true)
    private String paymentKey;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email", length = 50)
    private String userEmail;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "order_id", unique = true, updatable = false)
    private UUID orderId;

    @Column(name = "order_name", length = 100)
    private String orderName;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 15)
    private PaymentMethod method;

    @Builder
    public Payment(
        UUID id,
        String paymentKey,
        Long userId,
        String userEmail,
        String userName,
        UUID orderId,
        String orderName,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod method
    ) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.orderId = orderId;
        this.orderName = orderName;
        this.amount = amount;
        this.status = status;
        this.method = method;
    }
}
