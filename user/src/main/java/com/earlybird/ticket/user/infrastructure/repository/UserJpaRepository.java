package com.earlybird.ticket.user.infrastructure.repository;

import com.earlybird.ticket.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
