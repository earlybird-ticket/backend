package com.earlybird.ticket.user.application.dto.command;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.user.domain.entity.Seller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateUserSellerCommand(
    String email,
    String password,
    String name,
    Role role,
    LocalDate birthDay,
    String businessNumber,
    String address,
    String phoneNumber
) {

    public Seller toSeller() {
        return Seller.sellerBuilder()
            .businessNumber(this.businessNumber)
            .email(this.email)
            .password(this.password)
            .name(this.name)
            .role(this.role)
            .birthDate(this.birthDay)
            .address(this.address)
            .phoneNumber(this.phoneNumber)
            .build();
    }
}
