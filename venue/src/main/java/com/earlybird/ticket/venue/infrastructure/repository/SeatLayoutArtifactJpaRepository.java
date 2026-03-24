package com.earlybird.ticket.venue.infrastructure.repository;


import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatLayoutArtifactJpaRepository extends JpaRepository<SeatLayoutArtifact, UUID> {

}
