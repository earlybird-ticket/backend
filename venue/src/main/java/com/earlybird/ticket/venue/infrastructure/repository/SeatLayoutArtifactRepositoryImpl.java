package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.SeatLayoutArtifactRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SeatLayoutArtifactRepositoryImpl implements SeatLayoutArtifactRepository {

    private final SeatLayoutArtifactJpaRepository seatLayoutArtifactJpaRepository;

    @Override
    public Optional<SeatLayoutArtifact> findActiveArtifact(
        UUID concertId, UUID concertSequenceId, Section section
    ) {
        return seatLayoutArtifactJpaRepository
            .findByConcertIdAndConcertSequenceIdAndSectionAndIsActiveIsTrue(
                concertId, concertSequenceId, section
            );
    }

    @Override
    public Optional<SeatLayoutArtifact> findByScopeAndHashAndSchemaVersion(
        UUID concertId, UUID concertSequenceId, Section section, String hash, String schemaVersion
    ) {
        return seatLayoutArtifactJpaRepository
            .findByConcertIdAndConcertSequenceIdAndSectionAndHashAndSchemaVersion(
                concertId, concertSequenceId, section, hash, schemaVersion
            );
    }

    @Override
    public SeatLayoutArtifact save(SeatLayoutArtifact seatLayoutArtifact) {
        return seatLayoutArtifactJpaRepository.save(seatLayoutArtifact);
    }

    @Override
    public List<SeatLayoutArtifact> findActiveArtifactByConcertSequenceId(
        UUID concertSequenceId
    ) {
        return seatLayoutArtifactJpaRepository.findByConcertSequenceIdAndIsActiveIsTrue(
            concertSequenceId
        );
    }

}
