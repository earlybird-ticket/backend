package com.earlybird.ticket.user.application.dto.command;

import com.earlybird.ticket.user.domain.entity.User;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UpdateUserCustomerCommand(
    Long userId,
    String email,
    String name,
    LocalDate birthDay,
    String address,
    String phoneNumber
) {

    public User toUser() {
        return User.builder()
            .id(this.userId)
            .email(this.email)
            .name(this.name)
            .birthDay(this.birthDay)
            .address(this.address)
            .phoneNumber(this.phoneNumber)
            .build();
    }
}
