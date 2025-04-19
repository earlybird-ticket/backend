package com.earlybird.ticket.coupon.presentation.controller;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.coupon.application.dto.ProcessCouponQuery;
import com.earlybird.ticket.coupon.application.dto.ProcessUserCouponQuery;
import com.earlybird.ticket.coupon.application.service.CouponService;
import com.earlybird.ticket.coupon.presentation.dto.ProcessCouponReserveRequest;
import com.earlybird.ticket.coupon.presentation.dto.ProcessCouponResponse;
import com.earlybird.ticket.coupon.presentation.dto.ProcessUserCouponResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/users")
    public ResponseEntity<CommonDto<ProcessUserCouponResponse>> processUserCouponList(
            @RequestHeader(value = "X-User-Passport") String passport
    ) {
        ProcessUserCouponQuery couponList = couponService.getUserCouponList(passport);

        ProcessUserCouponResponse response = ProcessUserCouponResponse.of(couponList);

        return ResponseEntity.ok(CommonDto.ok(response, "회원 쿠폰 리스트 조회 성공"));
    }

    @GetMapping
    public ResponseEntity<CommonDto<List<ProcessCouponResponse>>> processCouponList(
            @RequestHeader(value = "X-User-Passport") String passport
    ) {
        List<ProcessCouponQuery> couponList = couponService.getCouponList(passport);
        List<ProcessCouponResponse> response = couponList.stream()
                .map((coupon) -> ProcessCouponResponse.builder()
                        .couponId(coupon.couponId())
                        .couponName(coupon.couponName())
                        .couponType(coupon.couponType())
                        .discountRate(coupon.discountRate())
                        .build())
                .toList();
        return ResponseEntity.ok(CommonDto.ok(response, "쿠폰 리스트 조회 성공"));
    }

    @PostMapping
    public ResponseEntity<CommonDto<Void>> processCouponReserve(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody ProcessCouponReserveRequest request
    ) {

        couponService.reserveCoupon(passport, request.toCommand());

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(CommonDto.accepted(null, "쿠폰 예약 성공"));
    }

}
