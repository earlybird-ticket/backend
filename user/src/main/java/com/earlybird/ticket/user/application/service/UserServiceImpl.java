package com.earlybird.ticket.user.application.service;

import com.earlybird.ticket.user.application.dto.command.CreateUserCustomerCommand;
import com.earlybird.ticket.user.application.dto.command.CreateUserSellerCommand;
import com.earlybird.ticket.user.application.dto.command.ProcessUserEmailValidateCommand;
import com.earlybird.ticket.user.application.exception.UserEmailDuplicatedException;
import com.earlybird.ticket.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
}
