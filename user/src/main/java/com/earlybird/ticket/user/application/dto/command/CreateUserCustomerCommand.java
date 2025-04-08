package com.earlybird.ticket.user.application.dto.command;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.user.domain.entity.User;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateUserCustomerCommand(
    String email,
    String password,
    String name,
    Role role,
    LocalDate birthDay,
    String address,
    String phoneNumber
) {

    public User toUser() {
        return User.builder()
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
