package com.earlybird.ticket.venue.domain.repository;

import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatLayoutArtifactRepository {

    Optional<SeatLayoutArtifact> findActiveArtifact(
        UUID concertId, UUID concertSequenceId, Section section
    );

    Optional<SeatLayoutArtifact> findByScopeAndHashAndSchemaVersion(
        UUID concertId, UUID concertSequenceId, Section section, String hash, String schemaVersion
    );

    SeatLayoutArtifact save(SeatLayoutArtifact seatLayoutArtifact);

    List<SeatLayoutArtifact> findActiveArtifactByConcertSequenceId(UUID concertSequenceId);

    Optional<SeatLayoutArtifact> findById(UUID artifactId);
}
