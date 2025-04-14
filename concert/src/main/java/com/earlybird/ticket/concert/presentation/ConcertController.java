package com.earlybird.ticket.concert.presentation;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.concert.application.ConcertService;
import com.earlybird.ticket.concert.presentation.dto.DeleteConcertRequest;
import com.earlybird.ticket.concert.presentation.dto.DeleteConcertSequenceRequest;
import com.earlybird.ticket.concert.presentation.dto.ProcessConcertDetailsRequest;
import com.earlybird.ticket.concert.presentation.dto.ProcessConcertRequest;
import com.earlybird.ticket.concert.presentation.dto.RegisterConcertRequest;
import com.earlybird.ticket.concert.presentation.dto.UpdateConcertRequest;
import com.earlybird.ticket.concert.presentation.dto.UpdateConcertSequenceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/concerts")
public class ConcertController {

    private final ConcertService concertService;

    @PostMapping("/register")
    public ResponseEntity<CommonDto<Void>> registerConcert(
            @RequestHeader("X-User-Passport") String passport,
            @RequestBody @Valid RegisterConcertRequest request
    ) {
        concertService.registerConcert(passport, request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonDto.created(null, "콘서트 생성 성공"));
    }

    @GetMapping("/")
    public ResponseEntity<CommonDto<Void>> processConcerts(
            @RequestHeader("X-User-Passport") String passport,
            @RequestBody @Valid ProcessConcertRequest request
    ) {
        concertService.processConcerts(passport, request.toCommand());
        return ResponseEntity.status(HttpStatus.OK).body(CommonDto.ok(null, "콘서트 조회 성공"));
    }

    @GetMapping("/detail")
    public ResponseEntity<CommonDto<Void>> processConcertDetail(
            @RequestHeader("X-User-Passport") String passport,
            @RequestBody @Valid ProcessConcertDetailsRequest request
    ) {
        concertService.processConcertDetail(passport, request.toCommand());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonDto.ok(null, "콘서트 상세 조회 성공"));
    }

    @PutMapping("/")
    public ResponseEntity<CommonDto<Void>> modifyConcert(
            @RequestHeader("X-User-Passport") String passport,
            @RequestBody @Valid UpdateConcertRequest request
    ) {
        concertService.modifyConcert(passport, request.toCommand());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(CommonDto.accepted(null, "콘서트 수정 성공"));
    }

    @PutMapping("/sequence")
    public ResponseEntity<CommonDto<Void>> modifyConcertSequence(
            @RequestHeader("X-User-Passport") String passport,
            @RequestBody @Valid UpdateConcertSequenceRequest request
    ) {
        concertService.modifyConcertSequence(passport, request.toCommand());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(CommonDto.accepted(null, "콘서트 회차 수정 성공"));
    }

    @DeleteMapping("/")
    public ResponseEntity<CommonDto<Void>> deleteConcert(
            @RequestHeader("X-User-Passport") String passport,
            @RequestBody @Valid DeleteConcertRequest request
    ) {
        concertService.deleteConcert(passport, request.toCommand());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(CommonDto.accepted(null, "콘서트 삭제 성공"));
    }

    @DeleteMapping("/sequence")
    public ResponseEntity<CommonDto<Void>> deleteConcertSequence(
            @RequestHeader("X-User-Passport") String passport,
            @RequestBody @Valid DeleteConcertSequenceRequest request
    ) {
        concertService.deleteConcertSequence(passport, request.toCommand());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(CommonDto.accepted(null, "콘서트 회차 삭제 성공"));
    }
}
