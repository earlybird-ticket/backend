package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.SeatLayoutArtifactRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatLayoutArtifactService {

    private final SeatLayoutArtifactRepository seatLayoutArtifactRepository;

    @Transactional
    public SeatLayoutArtifact prepareArtifactMetadata(
        UUID concertId,
        UUID concertSequenceId,
        Section section,
        String cdnUrl,
        String storageKey,
        String hash,
        String schemaVersion
    ) {
        return seatLayoutArtifactRepository
            .findByScopeAndHashAndSchemaVersion(
                concertId, concertSequenceId, section, hash, schemaVersion
            )
            .orElseGet(() -> seatLayoutArtifactRepository.save(
                SeatLayoutArtifact.builder()
                    .concertId(concertId)
                    .concertSequenceId(concertSequenceId)
                    .section(section)
                    .cdnUrl(cdnUrl)
                    .storageKey(storageKey)
                    .hash(hash)
                    .schemaVersion(schemaVersion)
                    .isActive(false)
                    .isReady(false)
                    .build()
            ));

    }

    @Transactional
    public SeatLayoutArtifact markReadyAndActivate(SeatLayoutArtifact artifact) {
        seatLayoutArtifactRepository.findActiveArtifact(
                artifact.getConcertId(),
                artifact.getConcertSequenceId(),
                artifact.getSection()
            )
            .filter(current -> !current.getId().equals(artifact.getId()))
            .ifPresent(SeatLayoutArtifact::deactivate);

        artifact.markReady();
        artifact.activate();

        return artifact;

    }

    public Map<Section, SeatLayoutArtifact> findActiveArtifactsByConcertSequenceId(
        UUID concertSequenceId) {
        return seatLayoutArtifactRepository.findActiveArtifactByConcertSequenceId(
                concertSequenceId
            )
            .stream()
            .collect(Collectors.toMap(
                SeatLayoutArtifact::getSection,
                Function.identity()
            ));
    }

}
