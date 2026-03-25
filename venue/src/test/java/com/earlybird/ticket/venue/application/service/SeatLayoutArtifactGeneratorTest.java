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
import java.util.List;
import java.util.UUID;
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
        List<WarmupSeatResult> firstData = warmupSeatResults(concertId, concertSequenceId);
        List<WarmupSeatResult> secondData = warmupSeatResults(concertId, concertSequenceId);

        // when
        List<SeatLayoutV1> firstArtifacts = seatLayoutArtifactGenerator.makeArtifacts(
            concertSequenceId, firstData);
        List<SeatLayoutV1> secondArtifacts = seatLayoutArtifactGenerator.makeArtifacts(
            concertSequenceId, secondData);

        List<String> firstHashes = firstArtifacts.stream()
            .map(seatLayoutArtifactGenerator::generateHash)
            .toList();

        List<String> secondHashes = secondArtifacts.stream()
            .map(seatLayoutArtifactGenerator::generateHash)
            .toList();

        List<String> firstKeys = firstArtifacts.stream()
            .map(artifact ->
                seatLayoutArtifactGenerator.generateStorageKey(
                    artifact, seatLayoutArtifactGenerator.generateHash(artifact)
                )
            ).toList();

        List<String> secondKeys = secondArtifacts.stream()
            .map(artifact ->
                seatLayoutArtifactGenerator.generateStorageKey(
                    artifact, seatLayoutArtifactGenerator.generateHash(artifact)
                )
            ).toList();

        // then
        assertThat(firstArtifacts).containsExactlyElementsOf(secondArtifacts);
        assertThat(firstHashes).containsExactlyElementsOf(secondHashes);
        assertThat(firstKeys).containsExactlyElementsOf(secondKeys);

    }

    @Test
    void 서로_다른_섹션이_포함되면_섹션별_아티팩트로_분리된다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        List<WarmupSeatResult> data = warmupSeatResults(concertId, concertSequenceId);

        // when
        List<SeatLayoutV1> artifacts = seatLayoutArtifactGenerator.makeArtifacts(
            concertSequenceId, data);

        SeatLayoutV1 sectionA = artifacts.stream()
            .filter(artifact -> artifact.section().equals(Section.A))
            .findFirst()
            .orElseThrow();

        SeatLayoutV1 sectionB = artifacts.stream()
            .filter(artifact -> artifact.section().equals(Section.B))
            .findFirst()
            .orElseThrow();

        // then
        assertThat(artifacts).filteredOn(artifact -> artifact.section() == Section.A)
            .hasSize(1);
        assertThat(artifacts).filteredOn(artifact -> artifact.section() == Section.B)
            .hasSize(1);

        assertThat(sectionA.seats()).extracting(
            SeatLayoutItem::row,
            SeatLayoutItem::col
        ).containsExactly(
            tuple(1, 1),
            tuple(1, 2)
        );

        assertThat(sectionB.seats()).extracting(
            SeatLayoutItem::row,
            SeatLayoutItem::col
        ).containsExactly(
            tuple(1, 1),
            tuple(2, 1)
        );

    }


    @Test
    void 아티팩트_해시를_포함한_스토리지_키를_생성한다() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        List<WarmupSeatResult> seatResults = warmupSeatResults(concertId, concertSequenceId);

        // when
        List<SeatLayoutV1> artifacts = seatLayoutArtifactGenerator.makeArtifacts(
            concertSequenceId, seatResults);

        // then
        assertThat(artifacts).allSatisfy(artifact -> {
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
        });

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
                BigDecimal.valueOf(10000),
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
}