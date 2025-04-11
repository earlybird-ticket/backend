package com.earlybird.ticket.reservation.presentation;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.application.service.ReservationService;
import com.earlybird.ticket.reservation.presentation.dto.request.CreateReservationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/reservations")
public class ReservationExternalController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CommonDto<Void>> createReservation(@RequestHeader("X-User-Passport") String passport,
                                                             @RequestBody @Valid List<CreateReservationRequest> createReservationRequest) {

        reservationService.createResrvation(createReservationRequest.stream()
                                                                    .map(CreateReservationRequest::toCreateReservationCommand

                                                                    )
                                                                    .toList(),
                                            passport);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CommonDto.created(null,
                                                     "예약 생성 성공"));
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<CommonDto<Void>> cancelReservation(@PathVariable String reservationId,
                                                             @RequestHeader("X-User-Passport") String passport) {
        reservationService.cancelReservation(reservationId,
                                             passport);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CommonDto.accepted(null,
                                                      "예약 취소 성공"));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<CommonDto<Void>> findReservations(@PathVariable String reservationId,
                                                            @RequestHeader("X-User-Passport") String passport) {

        FindReservationQuery findReservationQuery = reservationService.findReservation(reservationId,
                                                                                       passport);

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
