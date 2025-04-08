package com.earlybird.ticket.auth.application.dto.commander;

import lombok.Builder;

public record GetUserIdPasswordRoleCommand(
    String email
) {

    @Builder
    public GetUserIdPasswordRoleCommand(String email) {
        this.email = email;
    }
}
