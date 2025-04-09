package com.earlybird.ticket.user.presentation.dto.request;

import com.earlybird.ticket.user.application.dto.command.UpdateUserCustomerCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UpdateUserCustomerRequest(
    @NotNull(message = "userId is Necessary") Long userId,
    @Email(message = "email is Necessary") String email,
    @NotEmpty(message = "name is Necessary") String name,
    @NotNull(message = "birthDay is Necessary") LocalDate birthDay,
    @NotEmpty(message = "address is Necessary") String address,
    @NotEmpty(message = "phoneNumber is Necessary") String phoneNumber
) {

    public UpdateUserCustomerCommand toUpdateUserCustomerCommand() {
        return UpdateUserCustomerCommand.builder()
            .userId(this.userId)
            .email(this.email)
            .name(this.name)
            .birthDay(this.birthDay)
            .address(this.address)
            .phoneNumber(this.phoneNumber)
            .build();
    }
}
