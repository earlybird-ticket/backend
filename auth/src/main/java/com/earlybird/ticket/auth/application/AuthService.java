package com.earlybird.ticket.auth.application;

import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;

public interface AuthService {
    void createUser(CreateUserCommand createUserCommand);

    void createSeller(CreateSellerCommand createUserCommand);
}
