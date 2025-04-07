package com.earlybird.ticket.user.infrastructure.repository;

import com.earlybird.ticket.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

}
