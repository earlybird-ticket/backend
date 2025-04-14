package com.earlybird.ticket.concert.infrastructure.repository;

import com.earlybird.ticket.concert.domain.Concert;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, UUID> {

}
