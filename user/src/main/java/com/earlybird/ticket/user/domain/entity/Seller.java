package com.earlybird.ticket.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SELLER")
@Table(name = "p_seller")
@SuperBuilder(builderMethodName = "sellerBuilder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seller extends User {

    @Column(name = "business_number", nullable = false, length = 20)
    private String businessNumber;
}
