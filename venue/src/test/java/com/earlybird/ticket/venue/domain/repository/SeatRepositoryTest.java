package com.earlybird.ticket.venue.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.earlybird.ticket.common.configuration.QueryDslConfig;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.earlybird.ticket.venue.infrastructure.repository.SeatQueryRepositoryImpl;
import com.earlybird.ticket.venue.infrastructure.repository.SeatRepositoryImpl;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({SeatRepositoryImpl.class, SeatQueryRepositoryImpl.class, QueryDslConfig.class})
@Sql(value = "/sql/cleanup.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/seat-repository-fixture.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Test
    void 공연회차_목록으로_warmup_좌석정보를_조회한다() {
        // given
        UUID concertSequenceId = UUID.fromString("70000000-0000-0000-0000-000000000001");

        // when
        List<WarmupSeatResult> results = seatRepository.findSeatInfoByConcertSequenceIdList(
            List.of(concertSequenceId));

        // then
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(3);
        assertThat(results).allSatisfy(result -> {
            assertThat(result.concertId()).isNotNull();
            assertThat(result.concertSequenceId()).isEqualTo(concertSequenceId);
            assertThat(result.seatId()).isNotNull();
            assertThat(result.seatInstanceId()).isNotNull();
            assertThat(result.section()).isNotNull();
            assertThat(result.row()).isNotNull();
            assertThat(result.col()).isNotNull();
            assertThat(result.floor()).isNotNull();
            assertThat(result.grade()).isNotNull();
            assertThat(result.price()).isNotNull();
            assertThat(result.status()).isNotNull();
        });

    }

    @Test
    void warmup_좌석정보_조회시_필드가_정확히_매핑된다() {
        // given
        UUID seatId = UUID.fromString("50000000-0000-0000-0000-000000000001");
        UUID seatInstanceId = UUID.fromString("60000000-0000-0000-0000-000000000001");
        UUID concertId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID concertSequenceId = UUID.fromString("70000000-0000-0000-0000-000000000001");
        Section section = Section.A;
        int row = 1;
        int col = 1;
        int floor = 1;
        Grade grade = Grade.R;
        BigDecimal price = new BigDecimal("100000");
        Status status = Status.FREE;

        // when
        List<WarmupSeatResult> results = seatRepository.findSeatInfoByConcertSequenceIdList(
            List.of(concertSequenceId));

        // then
        assertThat(results).isNotEmpty();

        WarmupSeatResult first = results.stream()
            .filter(r -> r.seatInstanceId().equals(seatInstanceId))
            .findFirst()
            .orElseThrow();

        assertThat(first.seatId()).isEqualTo(seatId);
        assertThat(first.seatInstanceId()).isEqualTo(seatInstanceId);
        assertThat(first.concertId()).isEqualTo(concertId);
        assertThat(first.concertSequenceId()).isEqualTo(concertSequenceId);
        assertThat(first.section()).isEqualTo(section);
        assertThat(first.row()).isEqualTo(row);
        assertThat(first.col()).isEqualTo(col);
        assertThat(first.floor()).isEqualTo(floor);
        assertThat(first.grade()).isEqualTo(grade);
        assertThat(first.price()).isEqualByComparingTo(price);
        assertThat(first.status()).isEqualTo(status);
    }

    @Test
    void warmup_좌석정보는_공연회차_섹션_행_열_순으로_정렬된다() {
        // given
        List<UUID> concertSequenceIds = List.of(
            UUID.fromString("70000000-0000-0000-0000-000000000001"),
            UUID.fromString("70000000-0000-0000-0000-000000000002")
        );

        // when
        List<WarmupSeatResult> results = seatRepository.findSeatInfoByConcertSequenceIdList(
            concertSequenceIds);

        List<WarmupSeatResult> expected = results.stream()
            .sorted(Comparator.comparing(WarmupSeatResult::concertSequenceId)
                .thenComparing(WarmupSeatResult::section)
                .thenComparing(WarmupSeatResult::row)
                .thenComparing(WarmupSeatResult::col)
            ).toList();

        // then
        assertThat(results).isNotEmpty();
        assertThat(results).filteredOn(r -> r.concertSequenceId().equals(concertSequenceIds.get(0)))
            .hasSize(3);
        assertThat(results).filteredOn(r -> r.concertSequenceId().equals(concertSequenceIds.get(1)))
            .hasSize(1);
        assertThat(results).containsExactlyElementsOf(expected);
    }

}
