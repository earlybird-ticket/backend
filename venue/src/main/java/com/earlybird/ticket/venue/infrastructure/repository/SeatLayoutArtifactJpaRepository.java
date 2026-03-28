package com.earlybird.ticket.venue.infrastructure.repository;


import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatLayoutArtifactJpaRepository extends JpaRepository<SeatLayoutArtifact, UUID> {

    Optional<SeatLayoutArtifact> findByConcertIdAndConcertSequenceIdAndSectionAndIsActiveIsTrue(
        UUID concertId, UUID concertSequenceId, Section section
    );

    Optional<SeatLayoutArtifact> findByConcertIdAndConcertSequenceIdAndSectionAndHashAndSchemaVersion(
        UUID concertId, UUID concertSequenceId, Section section, String hash, String schemaVersion);

    List<SeatLayoutArtifact> findByConcertSequenceIdAndIsActiveIsTrue(UUID concertSequenceId);
}
