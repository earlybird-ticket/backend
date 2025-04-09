package com.earlybird.ticket.user.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.user.application.dto.command.CreateUserCustomerCommand;
import com.earlybird.ticket.user.application.dto.command.CreateUserSellerCommand;
import com.earlybird.ticket.user.application.dto.command.ProcessUserEmailValidateCommand;
import com.earlybird.ticket.user.application.dto.command.UpdateUserCustomerCommand;
import com.earlybird.ticket.user.application.dto.command.UpdateUserCustomerPasswordCommand;
import com.earlybird.ticket.user.application.dto.query.FindUserQuery;
import com.earlybird.ticket.user.application.dto.query.GetUserIdPasswordRoleQuery;
import com.earlybird.ticket.user.application.exception.UserEmailDuplicatedException;
import com.earlybird.ticket.user.application.exception.UserNotFoundException;
import com.earlybird.ticket.user.domain.entity.User;
import com.earlybird.ticket.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PassportUtil passportUtil;

    @Override
    public void findUserEmailDuplicated(
        ProcessUserEmailValidateCommand processUserEmailValidateCommand
    ) {
        userRepository.findUserByUserEmail(processUserEmailValidateCommand.Email())
            .ifPresent(user -> {
                throw new UserEmailDuplicatedException();
            });
    }

    @Override
    public void createUserSeller(CreateUserSellerCommand sellerCommand) {
        userRepository.save(sellerCommand.toSeller());
    }

    @Transactional
    @Override
    public void createUserCustomer(CreateUserCustomerCommand customerCommand) {
        userRepository.save(customerCommand.toUser());
    }

    @Override
    public FindUserQuery findUser(String passport) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);

        User user = userRepository.findUserByUserId(passportDto.getUserId())
            .orElseThrow(UserNotFoundException::new);

        return FindUserQuery.of(user);
    }

    @Override
    public GetUserIdPasswordRoleQuery findUserByEmail(String email) {
        User user = userRepository.findUserByUserEmail(email)
            .orElseThrow(UserNotFoundException::new);

        return GetUserIdPasswordRoleQuery.of(user);
    }

    @Override
    @Transactional
    public void updateUserCustomer(UpdateUserCustomerCommand updateUserCustomerCommand) {
        User user = userRepository.findUserByUserId(updateUserCustomerCommand.userId())
            .orElseThrow(UserNotFoundException::new);

        user.updateCustomerWithoutPassword(updateUserCustomerCommand.toUser());
    }

    @Override
    @Transactional
    public void updateUserCustomerPassword(
        UpdateUserCustomerPasswordCommand updateCustomerPasswordCommand
    ) {
        User user = userRepository.findUserByUserId(updateCustomerPasswordCommand.userId())
            .orElseThrow(UserNotFoundException::new);

        user.updatePassword(updateCustomerPasswordCommand.password());
    }
}
