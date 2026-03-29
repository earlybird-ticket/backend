package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.request.SeatLayoutV1;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;

@RequiredArgsConstructor
public class SeatLayoutArtifactGenerator {

    private final ObjectMapper artifactObjectMapper;

    /**
     * 반드시 동일 공연 회차 및 동일 섹션이며, 행/열 순으로 정렬된 입력이 들어와야 한다.
     */
    public SeatLayoutV1 makeArtifact(
        UUID concertSequenceId,
        List<WarmupSeatResult> warmupSeatResults
    ) {

        if (warmupSeatResults == null || warmupSeatResults.isEmpty()) {
            throw new IllegalArgumentException("좌석 레이아웃 생성 입력은 비어 있을 수 없습니다.");
        }

        Section currentSection = warmupSeatResults.get(0).section();
        UUID concertId = warmupSeatResults.get(0).concertId();

        List<SeatLayoutV1.SeatLayoutItem> items = warmupSeatResults.stream()
            .map(SeatLayoutV1.SeatLayoutItem::toSeatLayoutItem)
            .toList();

        return SeatLayoutV1.builder()
            .concertId(concertId)
            .concertSequenceId(concertSequenceId)
            .section(currentSection)
            .seats(items)
            .build();
    }

    public byte[] toCanonicalJsonBytes(SeatLayoutV1 seatLayoutV1) {
        try {
            return artifactObjectMapper.writeValueAsBytes(seatLayoutV1);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("좌석 레이아웃 직렬화에 실패했습니다.", e);
        }
    }

    public String generateHash(SeatLayoutV1 seatLayoutV1) {
        return DigestUtils.md5DigestAsHex(toCanonicalJsonBytes(seatLayoutV1));
    }

    public String generateStorageKey(SeatLayoutV1 seatLayoutV1, String hash) {
        return String.format(
            "venue/%s/section-%s/%s-%s.json",
            seatLayoutV1.concertSequenceId().toString(),
            seatLayoutV1.section().getValue(),
            seatLayoutV1.schemaVersion(),
            hash
        );
    }
}
