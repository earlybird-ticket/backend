package com.earlybird.ticket.user.presentation.dto.request;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.user.application.dto.command.CreateUserCustomerCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record SignUpUserCustomerRequest(
    @Email(message = "Email can't be empty") String email,
    @NotEmpty(message = "Password can't be empty") String password,
    @NotEmpty(message = "name can't be empty") String name,
    @NotNull(message = "role can't be empty") Role role,
    @NotNull(message = "birthDay can't be null") LocalDate birthDay,
    String address,
    String phoneNumber
) {

    public CreateUserCustomerCommand createUserCustomerCommand() {
        return CreateUserCustomerCommand.builder()
            .email(this.email)
            .password(this.password)
            .name(this.name)
            .role(this.role)
            .birthDay(this.birthDay)
            .address(this.address)
            .phoneNumber(this.phoneNumber)
            .build();
    }
}
