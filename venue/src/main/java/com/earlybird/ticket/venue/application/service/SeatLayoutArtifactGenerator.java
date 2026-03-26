package com.earlybird.ticket.venue.application.service;

import com.earlybird.ticket.venue.application.dto.request.SeatLayoutV1;
import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class SeatLayoutArtifactGenerator {

    private final ObjectMapper artifactObjectMapper;

    /**
     * 반드시 동일 회차 공연이며, 섹션/행/열 순으로 정렬된 입력이 들어와야 한다.
     */
    public List<SeatLayoutV1> makeArtifacts(
        UUID concertSequenceId,
        List<WarmupSeatResult> warmupSeatResults
    ) {
        List<SeatLayoutV1> bucket = new ArrayList<>();
        List<SeatLayoutV1.SeatLayoutItem> items = new ArrayList<>();
        Section currentSection = null;
        UUID concertId = null;

        for (WarmupSeatResult warmupSeatResult : warmupSeatResults) {
            if (currentSection != null && currentSection != warmupSeatResult.section()) {
                bucket.add(
                    SeatLayoutV1.builder()
                        .concertId(concertId)
                        .concertSequenceId(concertSequenceId)
                        .section(currentSection)
                        .seats(items)
                        .build()
                );

                items.clear();
            }
            currentSection = warmupSeatResult.section();
            concertId = warmupSeatResult.concertId();
            items.add(SeatLayoutV1.SeatLayoutItem.toSeatLayoutItem(warmupSeatResult));
        }

        if (currentSection != null && !items.isEmpty()) {
            bucket.add(
                SeatLayoutV1.builder()
                    .concertId(concertId)
                    .concertSequenceId(concertSequenceId)
                    .section(currentSection)
                    .seats(items)
                    .build()
            );
        }

        return bucket;
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
