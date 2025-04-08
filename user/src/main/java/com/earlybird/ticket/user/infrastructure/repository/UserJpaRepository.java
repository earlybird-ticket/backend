package com.earlybird.ticket.user.infrastructure.repository;

import com.earlybird.ticket.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    // 다형성으로 인해 자식 타입까지 조회 가능
    Optional<User> findByEmail(String email);
}
