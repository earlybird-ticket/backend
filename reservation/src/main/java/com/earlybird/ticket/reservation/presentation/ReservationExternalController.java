package com.earlybird.ticket.reservation.presentation;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.common.util.PageUtil;
import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.application.service.ReservationService;
import com.earlybird.ticket.reservation.domain.dto.response.ReservationSearchResult;
import com.earlybird.ticket.reservation.presentation.dto.response.FindReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/reservations")
public class ReservationExternalController {

    private final ReservationService reservationService;
    //
    //    @PostMapping
    //    public ResponseEntity<CommonDto<String>> createReservation(@RequestHeader("X-User-Passport") String passport,
    //                                                               @RequestBody @Valid CreateReservationRequest createReservationRequest) {
    //
    //        String reservationId = reservationService.createReservation(CreateReservationRequest.toCreateReservationCommand(createReservationRequest),
    //                                                                    passport);
    //
    //        return ResponseEntity.status(HttpStatus.CREATED)
    //                             .body(CommonDto.created(reservationId,
    //                                                     "예약 생성 성공"));
    //    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<CommonDto<Void>> cancelReservation(@PathVariable UUID reservationId,
                                                             @RequestHeader("X-User-Passport") String passport) {
        reservationService.cancelReservation(reservationId,
                                             passport);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CommonDto.accepted(null,
                                                      "예약 취소 성공"));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<CommonDto<FindReservationResponse>> findReservations(@PathVariable UUID reservationId,
                                                                               @RequestHeader("X-User-Passport") String passport) {

        FindReservationQuery findReservationQuery = reservationService.findReservation(reservationId,
                                                                                       passport);
        FindReservationResponse findReservationResponse = FindReservationResponse.createFindReservationResponse(findReservationQuery);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(CommonDto.ok(findReservationResponse,
                                                "예약 조회 성공"));
    }

    @GetMapping("/search")
    public ResponseEntity<CommonDto<PagedModel<ReservationSearchResult>>> searchReservations(@RequestParam(value = "q") String q,
                                                                                             @RequestParam(value = "size") int size,
                                                                                             @RequestParam(value = "page") int page,
                                                                                             @RequestParam(value = "sort") String sort,
                                                                                             @RequestParam(value = "orderBy") String orderBy,
                                                                                             @RequestParam(value = "startTime") String startTime,
                                                                                             @RequestParam(value = "endTime") String endTime,
                                                                                             @RequestHeader("X-User-Passport") String passport) {

        Pageable pageable = PageUtil.getPageable(page,
                                                 size,
                                                 sort,
                                                 orderBy);

        Page<ReservationSearchResult> pageResult = reservationService.searchReservations(pageable,
                                                                                         q,
                                                                                         startTime,
                                                                                         endTime,
                                                                                         passport);

        PagedModel<ReservationSearchResult> pagedModel = new PagedModel<>(pageResult);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(CommonDto.ok(pagedModel,
                                                "예약 검색 성공"));
    }

    @GetMapping("/test")
    public ResponseEntity<CommonDto<Void>> test() {
        reservationService.test();

        return ResponseEntity.status(HttpStatus.OK)
                             .body(CommonDto.ok(null,
                                                "test"));
    }


}
