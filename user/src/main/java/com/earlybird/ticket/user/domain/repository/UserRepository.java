package com.earlybird.ticket.user.domain.repository;

import com.earlybird.ticket.user.domain.entity.Seller;
import com.earlybird.ticket.user.domain.entity.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findUserByUserEmail(String userEmail);

    void save(User user);
}
