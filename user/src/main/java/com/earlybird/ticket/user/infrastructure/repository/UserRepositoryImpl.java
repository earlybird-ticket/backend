package com.earlybird.ticket.user.infrastructure.repository;

import com.earlybird.ticket.user.domain.entity.User;
import com.earlybird.ticket.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findUserByUserEmail(String userEmail) {
        return userJpaRepository.findByEmail(userEmail);
    }
}
