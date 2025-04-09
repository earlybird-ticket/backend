package com.earlybird.ticket.user.presentation.dto.request;

import com.earlybird.ticket.user.application.dto.command.UpdateUserCustomerPasswordCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateUserCustomerPasswordRequest(
    @NotEmpty(message = "Password can't be empty") String password
) {

    public UpdateUserCustomerPasswordCommand toUpdateCustomerPasswordCommand() {
        return UpdateUserCustomerPasswordCommand.builder()
            .password(password)
            .build();
    }
}
