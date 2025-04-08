package com.earlybird.ticket.user.presentation.dto.request;

import com.earlybird.ticket.user.application.dto.command.ProcessUserEmailValidateCommand;
import jakarta.validation.constraints.NotNull;

public record FindUserEmailDuplicatedRequest(
    @NotNull(message = "이메일은 비어있을 수 없습니다.") String email
) {

    public ProcessUserEmailValidateCommand toProcessUserEmailValidateCommand() {
        return new ProcessUserEmailValidateCommand(email);
    }
}
