package com.earlybird.ticket.user.application.service;

import com.earlybird.ticket.user.application.dto.command.CreateUserCustomerCommand;
import com.earlybird.ticket.user.application.dto.command.CreateUserSellerCommand;
import com.earlybird.ticket.user.application.dto.command.ProcessUserEmailValidateCommand;
import com.earlybird.ticket.user.application.dto.query.FindUserQuery;
import com.earlybird.ticket.user.application.dto.query.GetUserIdPasswordRoleQuery;

public interface UserService {

    void findUserEmailDuplicated(ProcessUserEmailValidateCommand processUserEmailValidateCommand);

    void createUserCustomer(CreateUserCustomerCommand customerCommand);

    void createUserSeller(CreateUserSellerCommand sellerCommand);

    FindUserQuery findUser(String passport);

    GetUserIdPasswordRoleQuery findUserByEmail(String email);
}
