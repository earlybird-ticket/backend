package com.earlybird.ticket.admin.infrastructure.repository;

import com.earlybird.ticket.admin.domain.entity.Admin;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminJpaRepository extends JpaRepository<Admin, UUID> {

}
