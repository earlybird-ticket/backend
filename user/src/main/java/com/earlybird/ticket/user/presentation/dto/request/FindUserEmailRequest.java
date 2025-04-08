package com.earlybird.ticket.user.presentation.dto.request;

import jakarta.validation.constraints.Email;

public record FindUserEmailRequest(
    @Email(message = "Email can't be empty") String email
) {

}
