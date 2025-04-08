package com.earlybird.ticket.user.application.service;

import com.earlybird.ticket.user.application.dto.command.ProcessUserEmailValidateCommand;

public interface UserService {

    void findUserEmailDuplicated(ProcessUserEmailValidateCommand processUserEmailValidateCommand);
}
