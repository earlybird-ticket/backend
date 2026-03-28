package com.earlybird.ticket.venue.application.service;


import static org.assertj.core.api.Assertions.*;

import com.earlybird.ticket.venue.application.dto.request.SeatLayoutV1;
import com.earlybird.ticket.venue.application.dto.request.SeatLayoutV1.SeatLayoutItem;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class SeatLayoutArtifactGeneratorTest {

    private SeatLayoutArtifactGenerator seatLayoutArtifactGenerator;

    @BeforeEach
    void setup() {
        ObjectMapper artifactObjectMapper = JsonMapper.builder()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .build();

        seatLayoutArtifactGenerator = new SeatLayoutArtifactGenerator(artifactObjectMapper);
    }

    @Test
    void 정렬된_동일_입력으로_같은_아티팩트와_같은_해시를_생성한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        LinkedHashMap<Section, List<WarmupSeatResult>> firstBySection = getWarmupSeatResultsBySection(
            concertId, concertSequenceId
        );
        LinkedHashMap<Section, List<WarmupSeatResult>> secondBySection = getWarmupSeatResultsBySection(
            concertId, concertSequenceId
        );

        // when & then
        assertThat(firstBySection.keySet()).containsExactlyElementsOf(secondBySection.keySet());

        for (Section section : firstBySection.keySet()) {
            SeatLayoutV1 firstArtifact = seatLayoutArtifactGenerator.makeArtifact(
                concertSequenceId, firstBySection.get(section)
            );

            SeatLayoutV1 secondArtifact = seatLayoutArtifactGenerator.makeArtifact(
                concertSequenceId, secondBySection.get(section)
            );

            String firstHash = seatLayoutArtifactGenerator.generateHash(firstArtifact);
            String secondHash = seatLayoutArtifactGenerator.generateHash(secondArtifact);

            String firstKey = seatLayoutArtifactGenerator.generateStorageKey(
                firstArtifact, firstHash
            );

            String secondKey = seatLayoutArtifactGenerator.generateStorageKey(
                secondArtifact, secondHash
            );

            assertThat(firstArtifact).isEqualTo(secondArtifact);
            assertThat(firstHash).isEqualTo(secondHash);
            assertThat(firstKey).isEqualTo(secondKey);
        }

    }

    @Test
    void 동일_섹션_입력으로_아티팩트_필드를_생성한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;

        List<WarmupSeatResult> warmupSeatResults = List.of(
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000001"),
                UUID.fromString("40000000-0000-0000-0000-000000000001"),
                concertId,
                concertSequenceId,
                section,
                1,
                1,
                1,
                Grade.R,
                BigDecimal.valueOf(10000),
                Status.FREE
            ),
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000002"),
                UUID.fromString("40000000-0000-0000-0000-000000000002"),
                concertId,
                concertSequenceId,
                section,
                1,
                2,
                1,
                Grade.R,
                BigDecimal.valueOf(10000),
                Status.FREE
            )
        );

        // when
        SeatLayoutV1 artifact = seatLayoutArtifactGenerator.makeArtifact(
            concertSequenceId, warmupSeatResults);

        // then
        assertThat(artifact.section()).isEqualTo(Section.A);
        assertThat(artifact.concertId()).isEqualTo(concertId);
        assertThat(artifact.concertSequenceId()).isEqualTo(concertSequenceId);
        assertThat(artifact.schemaVersion()).isEqualTo("seat-layout-v1");

        assertThat(artifact.seats()).hasSize(warmupSeatResults.size());

        assertThat(artifact.seats()).extracting(
            SeatLayoutItem::row,
            SeatLayoutItem::col
        ).containsExactly(
            tuple(1, 1),
            tuple(1, 2)
        );

    }


    @Test
    void 아티팩트_해시를_포함한_스토리지_키를_생성한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        LinkedHashMap<Section, List<WarmupSeatResult>> warmupSeatResultsBySection =
            getWarmupSeatResultsBySection(concertId, concertSequenceId);

        for (Section section : warmupSeatResultsBySection.keySet()) {
            // when
            SeatLayoutV1 artifact = seatLayoutArtifactGenerator.makeArtifact(
                concertSequenceId, warmupSeatResultsBySection.get(section)
            );

            // then
            String hash = seatLayoutArtifactGenerator.generateHash(artifact);
            String storageKey = seatLayoutArtifactGenerator.generateStorageKey(artifact, hash);

            assertThat(storageKey).isEqualTo(
                "venue/%s/section-%s/%s-%s.json".formatted(
                    concertSequenceId,
                    artifact.section().getValue(),
                    artifact.schemaVersion(),
                    hash
                )
            );
        }
    }

    private List<WarmupSeatResult> warmupSeatResults(
        UUID concertId,
        UUID concertSequenceId
    ) {
        return List.of(
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000001"),
                UUID.fromString("40000000-0000-0000-0000-000000000001"),
                concertId,
                concertSequenceId,
                Section.A,
                1,
                1,
                1,
                Grade.R,
                BigDecimal.valueOf(10000),
                Status.FREE
            ),
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000002"),
                UUID.fromString("40000000-0000-0000-0000-000000000002"),
                concertId,
                concertSequenceId,
                Section.A,
                1,
                2,
                1,
                Grade.R,
                BigDecimal.valueOf(10000),
                Status.FREE
            ),
            new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000003"),
                UUID.fromString("40000000-0000-0000-0000-000000000003"),
                concertId,
                concertSequenceId,
                Section.B,
                1,
                1,
                1,
                Grade.R,
                BigDecimal.valueOf(12000),
                Status.FREE
            )
            , new WarmupSeatResult(
                UUID.fromString("50000000-0000-0000-0000-000000000004"),
                UUID.fromString("40000000-0000-0000-0000-000000000004"),
                concertId,
                concertSequenceId,
                Section.B,
                2,
                1,
                1,
                Grade.R,
                BigDecimal.valueOf(12000),
                Status.FREE
            )
        );
    }

    private LinkedHashMap<Section, List<WarmupSeatResult>> getWarmupSeatResultsBySection(
        UUID concertId,
        UUID concertSequenceId
    ) {
        return warmupSeatResults(concertId, concertSequenceId).stream()
            .collect(Collectors.groupingBy(
                    WarmupSeatResult::section,
                    LinkedHashMap::new,
                    Collectors.toList()
                )
            );
    }
}