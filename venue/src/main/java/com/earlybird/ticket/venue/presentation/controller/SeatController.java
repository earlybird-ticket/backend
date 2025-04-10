package com.earlybird.ticket.venue.presentation.controller;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.venue.application.dto.response.ProcessSeatCheckQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.service.SeatService;
import com.earlybird.ticket.venue.presentation.dto.request.ProcessSeatCheckRequest;
import com.earlybird.ticket.venue.presentation.dto.response.ProcessSeatCheckResponse;
import com.earlybird.ticket.venue.presentation.dto.response.SeatListResponse;
import com.earlybird.ticket.venue.presentation.dto.response.SectionListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/check")
    public ResponseEntity<CommonDto<ProcessSeatCheckResponse>> checkSeat(
            @Valid @RequestBody ProcessSeatCheckRequest processSeatCheckRequest
    ) {
        ProcessSeatCheckQuery seatCheckQuery = seatService.checkSeat(processSeatCheckRequest.toCommand());

        return ResponseEntity.ok().body(
                CommonDto.ok(
                        ProcessSeatCheckResponse.from(seatCheckQuery),
                        "좌석 확인 완료"
                )
        );
    }

}
