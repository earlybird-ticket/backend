package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.SeatLayoutArtifactStorage;
import com.earlybird.ticket.venue.application.SeatWarmupCacheLoader;
import com.earlybird.ticket.venue.application.dto.request.SeatLayoutV1;
import com.earlybird.ticket.venue.application.dto.request.WarmupSeatCommand;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarmupSeatOrchestrator {

    private final SeatService seatService;

    private final SeatLayoutArtifactGenerator seatLayoutArtifactGenerator;

    private final SeatLayoutArtifactStorage seatLayoutArtifactStorage;

    private final SeatLayoutArtifactService seatLayoutArtifactService;

    private final SeatWarmupCacheLoader seatWarmupCacheLoader;


    /**
     반드시 중복되지 않은 공연 회차의 warmup 명령이 전달된다.
     */
    public void warmup(List<WarmupSeatCommand> warmupSeatCommands) {
        Map<UUID, WarmupSeatCommand> commandBySequenceId = warmupSeatCommands.stream()
            .collect(Collectors.toMap(
                WarmupSeatCommand::concertSequenceId,
                Function.identity()
            ));

        Map<UUID, List<WarmupSeatResult>> warmupSeatData =
            seatService.collectWarmupSeatData(new ArrayList<>(commandBySequenceId.keySet()));

        for (Map.Entry<UUID, List<WarmupSeatResult>> entry : warmupSeatData.entrySet()) {
            UUID concertSequenceId = entry.getKey();

            LinkedHashMap<Section, List<WarmupSeatResult>> warmupSeatResultsBySection =
                getWarmupSeatResultsBySection(entry.getValue());

            WarmupSeatCommand warmupSeatCommand = commandBySequenceId.get(concertSequenceId);
            if (warmupSeatCommand == null) {
                throw new IllegalStateException(
                    "warmup 명령이 없는 공연 회차입니다. concertSequenceId = " + concertSequenceId
                );
            }

            for (Section section : warmupSeatResultsBySection.keySet()) {
                List<WarmupSeatResult> seatResults = warmupSeatResultsBySection.get(section);

                SeatLayoutV1 artifact = seatLayoutArtifactGenerator.makeArtifact(
                    concertSequenceId, seatResults
                );

                String hash = seatLayoutArtifactGenerator.generateHash(artifact);

                String storageKey = seatLayoutArtifactGenerator.generateStorageKey(artifact, hash);

                if (!seatLayoutArtifactStorage.checkArtifactExists(storageKey)) {
                    byte[] payload = seatLayoutArtifactGenerator.toCanonicalJsonBytes(artifact);
                    seatLayoutArtifactStorage.storeArtifact(
                        storageKey, payload, hash, artifact.schemaVersion()
                    );
                }

                String url = seatLayoutArtifactStorage.resolveArtifactUrl(storageKey);

                SeatLayoutArtifact artifactMetadata = seatLayoutArtifactService.prepareArtifactMetadata(
                    artifact.concertId(),
                    artifact.concertSequenceId(),
                    artifact.section(),
                    url,
                    storageKey,
                    hash,
                    artifact.schemaVersion()
                );

                if (artifactMetadata.isReady()) {
                    continue;
                }

                seatWarmupCacheLoader.load(
                    seatResults,
                    warmupSeatCommand.ticketExpiredAt(),
                    warmupSeatCommand.vipTicketExpiredAt()
                );

                seatLayoutArtifactService.markReadyAndActivate(artifactMetadata);
            }
        }
    }

    private LinkedHashMap<Section, List<WarmupSeatResult>> getWarmupSeatResultsBySection(
        List<WarmupSeatResult> seatResults) {
        return seatResults
            .stream()
            .collect(Collectors.groupingBy(
                WarmupSeatResult::section,
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }

}
