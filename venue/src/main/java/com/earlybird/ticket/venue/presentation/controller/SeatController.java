package com.earlybird.ticket.venue.presentation.controller;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.service.SeatService;
import com.earlybird.ticket.venue.presentation.dto.response.SeatListResponse;
import com.earlybird.ticket.venue.presentation.dto.response.SectionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/external/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/{concert_sequence_id}")
    public ResponseEntity<CommonDto<SectionListResponse>> findSectionList(
            @PathVariable(name = "concert_sequence_id") UUID concertSequenceId
    ) {
        SectionListQuery sectionListQuery = seatService.findSectionList(concertSequenceId);

        return ResponseEntity.ok().body(
                CommonDto.ok(
                        SectionListResponse.from(sectionListQuery),
                        "섹션 리스트 조회 완료"
                )
        );
    }

    @GetMapping("/{concert_sequence_id}/sections/{section}")
    public ResponseEntity<CommonDto<SeatListResponse>> findSeatList(
            @PathVariable(name = "concert_sequence_id") UUID concertSequenceId,
            @PathVariable(name = "section") String section
    ) {
        SeatListQuery seatListQuery = seatService.findSeatList(concertSequenceId, section);

        return ResponseEntity.ok().body(
                CommonDto.ok(
                        SeatListResponse.from(seatListQuery),
                        "좌석 리스트 조회 완료"
                )
        );
    }

}
