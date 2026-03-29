package com.earlybird.ticket.venue.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery.SectionQuery;
import com.earlybird.ticket.venue.common.util.EventConverter;
import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import com.earlybird.ticket.venue.domain.repository.SeatRepository;
import com.earlybird.ticket.venue.infrastructure.redis.config.RedisConfig;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisKeyFactory;
import com.earlybird.ticket.venue.infrastructure.redis.util.RedisSectionListReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@ExtendWith(MockitoExtension.class)
class SeatServiceImplTest {

    private SeatServiceImpl seatService;

    @Mock
    private SeatRepository seatRepository;
    @Mock
    private OutboxRepository outboxRepository;
    @Mock
    private PassportUtil passportUtil;
    @Mock
    private EventConverter eventConverter;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private SetOperations<String, String> setOperations;
    @Mock
    private RedisConfig redisConfig;
    @Mock
    private RedisKeyFactory redisKeyFactory;
    @Mock
    private RedisSectionListReader redisSectionListReader;
    @Mock
    private SeatLayoutArtifactService seatLayoutArtifactService;

    @BeforeEach
    void setUp() {
        seatService = new SeatServiceImpl(
            seatRepository,
            outboxRepository,
            passportUtil,
            eventConverter,
            stringRedisTemplate,
            redisConfig,
            redisKeyFactory,
            redisSectionListReader,
            seatLayoutArtifactService
        );
    }

    @Test
    void 활성_아티팩트가_있으면_섹션_조회_응답에_layoutUrl과_schemaVersion을_포함한다() {
        // given
        UUID concertSequenceId = UUID.randomUUID();
        UUID concertId = UUID.randomUUID();

        List<String> sectionListKeys = List.of(
            RedisKeyFactory.SECTION_LIST_PREFIX + concertId + RedisKeyFactory.REDIS_COLON
                + concertSequenceId + RedisKeyFactory.REDIS_COLON + Section.A.getValue(),
            RedisKeyFactory.SECTION_LIST_PREFIX + concertId + RedisKeyFactory.REDIS_COLON
                + concertSequenceId + RedisKeyFactory.REDIS_COLON + Section.B.getValue()
        );

        BDDMockito.given(redisKeyFactory.getAllSectionListKeys(
                BDDMockito.eq(concertSequenceId), BDDMockito.anyLong())
            )
            .willReturn(sectionListKeys);

        List<SectionQuery> sectionQueries = List.of(
            SectionQuery.from(
                Section.A.getValue(),
                100,
                1,
                "A",
                new BigDecimal(10_000)
            ),
            SectionQuery.from(
                Section.B.getValue(),
                100,
                1,
                "S",
                new BigDecimal(12_000)
            )
        );

        BDDMockito.given(redisSectionListReader.read(sectionListKeys))
            .willReturn(sectionQueries);

        Map<Section, SeatLayoutArtifact> activeArtifactBySection = Map.of(
            Section.A,
            getSeatLayoutArtifact(
                concertId, concertSequenceId, Section.A, "test-cdn-url-A",
                "test-storage-key-A", "test-hash-A"),
            Section.B,
            getSeatLayoutArtifact(
                concertId, concertSequenceId, Section.B, "test-cdn-url-B",
                "test-storage-key-B", "test-hash-B")
        );

        BDDMockito.given(
                seatLayoutArtifactService.findActiveArtifactsByConcertSequenceId(concertSequenceId)
            )
            .willReturn(activeArtifactBySection);

        // when
        SectionListQuery sectionList = seatService.findSectionList(concertSequenceId);

        // then
        assertThat(sectionList.sectionList()).extracting(
            SectionQuery::cdnUrl,
            SectionQuery::schemaVersion,
            SectionQuery::section,
            SectionQuery::price
        ).containsExactly(
            tuple(
                "test-cdn-url-A", "seat-layout-v1", Section.A.getValue(), BigDecimal.valueOf(10_000)
            ),
            tuple(
                "test-cdn-url-B", "seat-layout-v1", Section.B.getValue(), BigDecimal.valueOf(12_000)
            )
        );

        BDDMockito.then(redisKeyFactory).should().getAllSectionListKeys(
            BDDMockito.eq(concertSequenceId), BDDMockito.anyLong()
        );
        BDDMockito.then(redisSectionListReader).should().read(sectionListKeys);
        BDDMockito.then(seatLayoutArtifactService).should().findActiveArtifactsByConcertSequenceId(
            concertSequenceId
        );

        BDDMockito.then(redisKeyFactory).shouldHaveNoMoreInteractions();
        BDDMockito.then(redisSectionListReader).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatLayoutArtifactService).shouldHaveNoMoreInteractions();
    }

    @Test
    void 활성_아티팩트가_없으면_섹션_조회_응답에_layoutUrl과_schemaVersion은_null이다() {
        // given
        UUID concertSequenceId = UUID.randomUUID();
        UUID concertId = UUID.randomUUID();

        List<String> sectionListKeys = List.of(
            RedisKeyFactory.SECTION_LIST_PREFIX + concertId + RedisKeyFactory.REDIS_COLON
                + concertSequenceId + RedisKeyFactory.REDIS_COLON + Section.A.getValue(),
            RedisKeyFactory.SECTION_LIST_PREFIX + concertId + RedisKeyFactory.REDIS_COLON
                + concertSequenceId + RedisKeyFactory.REDIS_COLON + Section.B.getValue()
        );

        BDDMockito.given(redisKeyFactory.getAllSectionListKeys(
                BDDMockito.eq(concertSequenceId), BDDMockito.anyLong())
            )
            .willReturn(sectionListKeys);

        List<SectionQuery> sectionQueries = List.of(
            SectionQuery.from(
                Section.A.getValue(),
                100,
                1,
                "A",
                new BigDecimal(10_000)
            ),
            SectionQuery.from(
                Section.B.getValue(),
                100,
                1,
                "S",
                new BigDecimal(12_000)
            )
        );

        BDDMockito.given(redisSectionListReader.read(sectionListKeys)).willReturn(sectionQueries);

        BDDMockito.given(
                seatLayoutArtifactService.findActiveArtifactsByConcertSequenceId(concertSequenceId)
            )
            .willReturn(Collections.emptyMap());

        // when
        SectionListQuery sectionList = seatService.findSectionList(concertSequenceId);

        // then
        assertThat(sectionList.sectionList()).extracting(
            SectionQuery::cdnUrl,
            SectionQuery::schemaVersion,
            SectionQuery::section,
            SectionQuery::price
        ).containsExactly(
            tuple(null, null, Section.A.getValue(), BigDecimal.valueOf(10_000)),
            tuple(null, null, Section.B.getValue(), BigDecimal.valueOf(12_000))
        );

        BDDMockito.then(redisKeyFactory).should().getAllSectionListKeys(
            BDDMockito.eq(concertSequenceId), BDDMockito.anyLong()
        );
        BDDMockito.then(redisSectionListReader).should().read(sectionListKeys);
        BDDMockito.then(seatLayoutArtifactService).should().findActiveArtifactsByConcertSequenceId(
            concertSequenceId
        );

        BDDMockito.then(redisKeyFactory).shouldHaveNoMoreInteractions();
        BDDMockito.then(redisSectionListReader).shouldHaveNoMoreInteractions();
        BDDMockito.then(seatLayoutArtifactService).shouldHaveNoMoreInteractions();
    }

    @Test
    void 가능한_좌석_인덱스가_있으면_인덱스_목록을_반환한다() {
        // given
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        String seatIndexKey = "seat-index-key";

        Set<String> indexes = Set.of("123", "1", "3");

        BDDMockito.given(
                redisKeyFactory.generateSeatIndexKey(concertSequenceId, section.getValue()))
            .willReturn(seatIndexKey);
        BDDMockito.given(stringRedisTemplate.opsForSet()).willReturn(setOperations);
        BDDMockito.given(setOperations.members(seatIndexKey)).willReturn(indexes);

        // when
        SeatListQuery seatList = seatService.findSeatList(concertSequenceId, section.getValue());

        // then
        assertThat(seatList.availableIndexes()).containsExactlyInAnyOrder(123, 1, 3);
        assertThat(seatList.section()).isEqualTo(section.getValue());

        BDDMockito.then(redisKeyFactory).should()
            .generateSeatIndexKey(concertSequenceId, section.getValue());
        BDDMockito.then(stringRedisTemplate).should().opsForSet();
        BDDMockito.then(setOperations).should().members(seatIndexKey);

        BDDMockito.then(setOperations).shouldHaveNoMoreInteractions();
        BDDMockito.then(redisKeyFactory).shouldHaveNoMoreInteractions();
        BDDMockito.then(stringRedisTemplate).shouldHaveNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("emptyOrNullMembers")
    void 가능한_좌석_인덱스_조회_결과가_없으면_빈_목록을_반환한다(Set<String> members) {
        // given
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        String seatIndexKey = "seat-index-key";

        BDDMockito.given(
                redisKeyFactory.generateSeatIndexKey(concertSequenceId, section.getValue())
            )
            .willReturn(seatIndexKey);
        BDDMockito.given(stringRedisTemplate.opsForSet()).willReturn(setOperations);
        BDDMockito.given(setOperations.members(seatIndexKey)).willReturn(members);

        // when
        SeatListQuery seatList = seatService.findSeatList(concertSequenceId, section.getValue());

        // then
        assertThat(seatList.availableIndexes()).isEmpty();
        assertThat(seatList.section()).isEqualTo(section.getValue());

        BDDMockito.then(redisKeyFactory).should()
            .generateSeatIndexKey(concertSequenceId, section.getValue());
        BDDMockito.then(stringRedisTemplate).should().opsForSet();
        BDDMockito.then(setOperations).should().members(seatIndexKey);

        BDDMockito.then(setOperations).shouldHaveNoMoreInteractions();
        BDDMockito.then(redisKeyFactory).shouldHaveNoMoreInteractions();
        BDDMockito.then(stringRedisTemplate).shouldHaveNoMoreInteractions();
    }

    private static Stream<Set<String>> emptyOrNullMembers() {
        return Stream.of(null, Collections.emptySet());
    }

    private static SeatLayoutArtifact getSeatLayoutArtifact(
        UUID concertId,
        UUID concertSequenceId,
        Section section,
        String cdnUrl,
        String storageKey,
        String hash
    ) {
        return SeatLayoutArtifact.builder()
            .id(UUID.randomUUID())
            .concertId(concertId)
            .concertSequenceId(concertSequenceId)
            .section(section)
            .cdnUrl(cdnUrl)
            .storageKey(storageKey)
            .hash(hash)
            .schemaVersion("seat-layout-v1")
            .isActive(true)
            .isReady(true)
            .build();
    }
}