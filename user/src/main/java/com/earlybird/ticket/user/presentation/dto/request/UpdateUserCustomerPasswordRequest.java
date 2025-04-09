package com.earlybird.ticket.user.presentation.dto.request;

import com.earlybird.ticket.user.application.dto.command.UpdateUserCustomerPasswordCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateUserCustomerPasswordRequest(
    @NotNull(message = "userId can't be null") Long userId,
    @NotEmpty(message = "Password can't be empty") String password
) {

    public UpdateUserCustomerPasswordCommand toUpdateCustomerPasswordCommand() {
        return UpdateUserCustomerPasswordCommand.builder()
            .userId(userId)
            .password(password)
            .build();
    }
}
