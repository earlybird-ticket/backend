package com.earlybird.ticket.venue.application.service;


import static org.assertj.core.api.Assertions.assertThatCode;

import com.earlybird.ticket.venue.application.SeatLayoutArtifactStorage;
import com.earlybird.ticket.venue.application.SeatWarmupCacheLoader;
import com.earlybird.ticket.venue.application.dto.request.SeatLayoutV1;
import com.earlybird.ticket.venue.application.dto.request.WarmupSeatCommand;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarmupSeatOrchestratorTest {

    private WarmupSeatOrchestrator warmupSeatOrchestrator;

    @Mock
    private SeatService seatService;

    private SeatLayoutArtifactGenerator seatLayoutArtifactGenerator;

    @Mock
    private SeatLayoutArtifactStorage seatLayoutArtifactStorage;

    @Mock
    private SeatLayoutArtifactService seatLayoutArtifactService;

    @Mock
    private SeatWarmupCacheLoader seatWarmupCacheLoader;

    @BeforeEach
    void setup() {
        ObjectMapper artifactObjectMapper = JsonMapper.builder()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .build();

        seatLayoutArtifactGenerator = new SeatLayoutArtifactGenerator(artifactObjectMapper);

        warmupSeatOrchestrator = new WarmupSeatOrchestrator(
            seatService,
            seatLayoutArtifactGenerator,
            seatLayoutArtifactStorage,
            seatLayoutArtifactService,
            seatWarmupCacheLoader
        );
    }

    @Test
    void 새_구조로_섹션별_warmup을_끝까지_수행한다() {
        // given
        WarmupFixture fixture = createFixture(false);
        stubCollectedData(fixture);
        stubArtifactStorage(false);
        stubPrepareArtifactMetadata(fixture);

        // when
        assertThatCode(() -> warmupSeatOrchestrator.warmup(fixture.warmupSeatCommands()))
            .doesNotThrowAnyException();

        // then
        verifySeatServiceCalled(fixture.concertSequenceId());

        for (Section section : fixture.artifactsBySection().keySet()) {
            List<WarmupSeatResult> seatResults = fixture.seatResultsBySection().get(section);
            SeatLayoutV1 artifact = fixture.artifactsBySection().get(section);
            String hash = fixture.hashBySection().get(section);
            String storageKey = fixture.storageKeyBySection().get(section);

            BDDMockito.then(seatLayoutArtifactStorage).should().checkArtifactExists(storageKey);

            BDDMockito.then(seatLayoutArtifactStorage).should().storeArtifact(
                BDDMockito.eq(storageKey),
                BDDMockito.any(byte[].class),
                BDDMockito.eq(hash),
                BDDMockito.eq(artifact.schemaVersion())
            );

            BDDMockito.then(seatLayoutArtifactStorage).should().resolveArtifactUrl(storageKey);

            BDDMockito.then(seatLayoutArtifactService).should().prepareArtifactMetadata(
                fixture.concertId(),
                fixture.concertSequenceId(),
                artifact.section(),
                "url:" + storageKey,
                storageKey,
                hash,
                artifact.schemaVersion()
            );

            BDDMockito.then(seatWarmupCacheLoader).should().load(
                seatResults, fixture.ticketExpiredAt(), fixture.vipTicketExpiredAt()
            );

            BDDMockito.then(seatLayoutArtifactService).should().markReadyAndActivate(
                fixture.preparedArtifactsBySection().get(section).getId()
            );
        }

        BDDMockito.then(seatLayoutArtifactService).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatWarmupCacheLoader).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatLayoutArtifactStorage).shouldHaveNoMoreInteractions();
    }


    @Test
    void CDN에_아티팩트가_이미_있으면_업로드를_건너뛰고_이후_단계를_수행한다() {
        // given
        WarmupFixture fixture = createFixture(false);
        stubCollectedData(fixture);
        stubArtifactStorage(true);
        stubPrepareArtifactMetadata(fixture);

        // when
        assertThatCode(() -> warmupSeatOrchestrator.warmup(fixture.warmupSeatCommands()))
            .doesNotThrowAnyException();

        // then
        verifySeatServiceCalled(fixture.concertSequenceId());

        for (Section section : fixture.seatResultsBySection().keySet()) {
            List<WarmupSeatResult> warmupSeatResults = fixture.seatResultsBySection().get(section);
            SeatLayoutV1 artifact = fixture.artifactsBySection().get(section);
            String hash = fixture.hashBySection().get(section);
            String storageKey = fixture.storageKeyBySection().get(section);

            BDDMockito.then(seatLayoutArtifactStorage).should().checkArtifactExists(storageKey);
            BDDMockito.then(seatLayoutArtifactStorage).should().resolveArtifactUrl(storageKey);

            BDDMockito.then(seatLayoutArtifactService).should().prepareArtifactMetadata(
                fixture.concertId(),
                fixture.concertSequenceId(),
                artifact.section(),
                "url:" + storageKey,
                storageKey,
                hash,
                artifact.schemaVersion()
            );

            BDDMockito.then(seatWarmupCacheLoader).should().load(
                warmupSeatResults,
                fixture.ticketExpiredAt(),
                fixture.vipTicketExpiredAt()
            );

            BDDMockito.then(seatLayoutArtifactService).should().markReadyAndActivate(
                fixture.preparedArtifactsBySection().get(section).getId()
            );
        }

        BDDMockito.then(seatLayoutArtifactStorage).should(BDDMockito.never()).storeArtifact(
            BDDMockito.anyString(),
            BDDMockito.any(byte[].class),
            BDDMockito.anyString(),
            BDDMockito.anyString()
        );
        BDDMockito.then(seatLayoutArtifactStorage).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatWarmupCacheLoader).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatLayoutArtifactService).shouldHaveNoMoreInteractions();
    }

    @Test
    void 메타데이터가_ready_상태면_Redis_적재와_active_전환을_건너뛴다() {
        // given
        WarmupFixture fixture = createFixture(true);
        stubCollectedData(fixture);
        stubArtifactStorage(false);
        stubPrepareArtifactMetadata(fixture);

        // when
        assertThatCode(() -> warmupSeatOrchestrator.warmup(fixture.warmupSeatCommands()))
            .doesNotThrowAnyException();

        // then
        verifySeatServiceCalled(fixture.concertSequenceId());

        for (Section section : fixture.artifactsBySection().keySet()) {
            SeatLayoutV1 artifact = fixture.artifactsBySection().get(section);
            String hash = fixture.hashBySection().get(section);
            String storageKey = fixture.storageKeyBySection().get(section);

            BDDMockito.then(seatLayoutArtifactStorage).should().checkArtifactExists(storageKey);
            BDDMockito.then(seatLayoutArtifactStorage).should().storeArtifact(
                BDDMockito.eq(storageKey),
                BDDMockito.any(byte[].class),
                BDDMockito.eq(hash),
                BDDMockito.eq(artifact.schemaVersion())
            );
            BDDMockito.then(seatLayoutArtifactStorage).should().resolveArtifactUrl(storageKey);

            BDDMockito.then(seatLayoutArtifactService).should().prepareArtifactMetadata(
                fixture.concertId(),
                fixture.concertSequenceId(),
                section,
                "url:" + storageKey,
                storageKey,
                hash,
                artifact.schemaVersion()
            );
        }

        BDDMockito.then(seatLayoutArtifactStorage).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatWarmupCacheLoader).should(BDDMockito.never()).load(
            BDDMockito.anyList(),
            BDDMockito.any(LocalDateTime.class),
            BDDMockito.any(LocalDateTime.class)
        );
        BDDMockito.then(seatWarmupCacheLoader).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatLayoutArtifactService).should(BDDMockito.never())
            .markReadyAndActivate(BDDMockito.any(UUID.class));
        BDDMockito.then(seatLayoutArtifactService).shouldHaveNoMoreInteractions();

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
                Grade.S,
                BigDecimal.valueOf(20_000),
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
                Grade.S,
                BigDecimal.valueOf(20_000),
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
                BigDecimal.valueOf(10_000),
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
                BigDecimal.valueOf(10_000),
                Status.FREE
            )
        );
    }

    private LinkedHashMap<Section, List<WarmupSeatResult>> getWarmupSeatResultsBySection(
        List<WarmupSeatResult> seatResults
    ) {
        return seatResults.stream()
            .collect(Collectors.groupingBy(
                    WarmupSeatResult::section,
                    LinkedHashMap::new,
                    Collectors.toList()
                )
            );
    }

    private void stubPrepareArtifactMetadata(WarmupFixture fixture) {
        BDDMockito.given(seatLayoutArtifactService.prepareArtifactMetadata(
            BDDMockito.any(UUID.class),
            BDDMockito.any(UUID.class),
            BDDMockito.any(Section.class),
            BDDMockito.anyString(),
            BDDMockito.anyString(),
            BDDMockito.anyString(),
            BDDMockito.anyString()
        )).willAnswer(invocation -> {
                Section section = invocation.getArgument(2);
                return fixture.preparedArtifactsBySection().get(section);
            }
        );
    }

    private void stubArtifactStorage(boolean value) {
        BDDMockito.given(seatLayoutArtifactStorage.checkArtifactExists(BDDMockito.anyString()))
            .willReturn(value);

        BDDMockito.given(seatLayoutArtifactStorage.resolveArtifactUrl(BDDMockito.anyString()))
            .willAnswer(invocation -> "url:" + invocation.getArgument(0));
    }

    private void stubCollectedData(WarmupFixture fixture) {
        BDDMockito.given(seatService.collectWarmupSeatData(List.of(fixture.concertSequenceId())))
            .willReturn(fixture.collectedData());
    }

    private void verifySeatServiceCalled(UUID concertSequenceId) {
        BDDMockito.then(seatService).should().collectWarmupSeatData(
            List.of(concertSequenceId)
        );
        BDDMockito.then(seatService).shouldHaveNoMoreInteractions();
    }

    private WarmupFixture createFixture(boolean ready) {
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();

        LocalDateTime ticketExpiredAt = LocalDateTime.of(2026, 3, 28, 10, 0);
        LocalDateTime vipTicketExpiredAt = LocalDateTime.of(2026, 3, 28, 9, 50);
        List<WarmupSeatCommand> warmupSeatCommands = List.of(
            new WarmupSeatCommand(concertSequenceId, ticketExpiredAt, vipTicketExpiredAt)
        );

        Map<UUID, List<WarmupSeatResult>> collectedData = Map.of(
            concertSequenceId, warmupSeatResults(concertId, concertSequenceId)
        );

        LinkedHashMap<Section, List<WarmupSeatResult>> seatResultsBySection =
            getWarmupSeatResultsBySection(collectedData.get(concertSequenceId));

        Map<Section, SeatLayoutV1> artifactsBySection = new LinkedHashMap<>();
        Map<Section, String> hashBySection = new LinkedHashMap<>();
        Map<Section, String> storageKeyBySection = new LinkedHashMap<>();
        Map<Section, SeatLayoutArtifact> preparedArtifactsBySection = new LinkedHashMap<>();

        for (Map.Entry<Section, List<WarmupSeatResult>> entry : seatResultsBySection.entrySet()) {
            SeatLayoutV1 artifact = seatLayoutArtifactGenerator.makeArtifact(
                concertSequenceId, entry.getValue()
            );
            String hash = seatLayoutArtifactGenerator.generateHash(artifact);
            String storageKey = seatLayoutArtifactGenerator.generateStorageKey(artifact, hash);

            artifactsBySection.put(entry.getKey(), artifact);
            hashBySection.put(entry.getKey(), hash);
            storageKeyBySection.put(entry.getKey(), storageKey);
            preparedArtifactsBySection.put(entry.getKey(), SeatLayoutArtifact.builder()
                .id(UUID.randomUUID())
                .concertId(concertId)
                .concertSequenceId(concertSequenceId)
                .section(entry.getKey())
                .cdnUrl("url:" + storageKey)
                .storageKey(storageKey)
                .hash(hash)
                .schemaVersion(artifact.schemaVersion())
                .isActive(ready)
                .isReady(ready)
                .build()
            );
        }
        return new WarmupFixture(
            concertId,
            concertSequenceId,
            ticketExpiredAt,
            vipTicketExpiredAt,
            warmupSeatCommands,
            collectedData,
            seatResultsBySection,
            artifactsBySection,
            hashBySection,
            storageKeyBySection,
            preparedArtifactsBySection
        );
    }

    private record WarmupFixture(
        UUID concertId,
        UUID concertSequenceId,
        LocalDateTime ticketExpiredAt,
        LocalDateTime vipTicketExpiredAt,
        List<WarmupSeatCommand> warmupSeatCommands,
        Map<UUID, List<WarmupSeatResult>> collectedData,
        LinkedHashMap<Section, List<WarmupSeatResult>> seatResultsBySection,
        Map<Section, SeatLayoutV1> artifactsBySection,
        Map<Section, String> hashBySection,
        Map<Section, String> storageKeyBySection,
        Map<Section, SeatLayoutArtifact> preparedArtifactsBySection
    ) {

    }
}