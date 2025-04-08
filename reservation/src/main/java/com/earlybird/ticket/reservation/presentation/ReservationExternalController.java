package com.earlybird.ticket.reservation.presentation;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.reservation.application.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/reservations")
public class ReservationExternalController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CommonDto<Void>> createReservation(@RequestHeader("X-User-Passport") String passport
                                                             //+dto
    ) {
        //1. CreateReservationCommand로 변환

        //2. reservationService.createResrvation(createReservationCommand)로 전달

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CommonDto.created(null,
                                                     "예약 생성 성공"));
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<CommonDto<Void>> cancelReservation(@PathVariable String reservationId,
                                                             @RequestHeader("X-User-Passport") String passport) {
        //1. CancelReservationCommand로 변환

        //2. reservationService.cancelResrvation(cancelReservationCommand로)로 전달

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CommonDto.accepted(null,
                                                      "예약 취소 성공"));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<CommonDto<Void>> findReservations(@PathVariable String reservationId,
                                                            @RequestHeader("X-User-Passport") String passport) {

        //1. reservationService.findResrvation(reservationId,passport)로 전달

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CommonDto.ok(null,
                                                "예약 조회 성공"));
    }

    @GetMapping("search")
    public ResponseEntity<CommonDto<Void>> searchReservations(@PathVariable String q,
                                                              @PathVariable int size,
                                                              @PathVariable String sort,
                                                              @PathVariable String order_by,
                                                              @PathVariable String startTime,
                                                              @PathVariable String endTime,
                                                              @RequestHeader("X-User-Passport") String passport) {

        //1. reservationService.searchResrvation(pageable객체)로 전달

        return ResponseEntity.status(HttpStatus.OK)
                             .body(CommonDto.ok(null,
                                                "예약 검색 성공"));
    }


}
