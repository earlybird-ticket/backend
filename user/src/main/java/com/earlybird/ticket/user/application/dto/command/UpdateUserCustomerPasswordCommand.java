package com.earlybird.ticket.user.application.dto.command;

import lombok.Builder;

@Builder
public record UpdateUserCustomerPasswordCommand(
    Long userId,
    String password
) {
}
