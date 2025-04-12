package com.earlybird.ticket.admin.presentation.controller;

import com.earlybird.ticket.admin.application.AdminService;
import com.earlybird.ticket.admin.presentation.dto.DeleteCouponRequest;
import com.earlybird.ticket.admin.presentation.dto.DeleteSeatInstanceRequest;
import com.earlybird.ticket.admin.presentation.dto.DeleteVenueRequest;
import com.earlybird.ticket.admin.presentation.dto.RegisterCouponIssueRequest;
import com.earlybird.ticket.admin.presentation.dto.RegisterCouponRequest;
import com.earlybird.ticket.admin.presentation.dto.RegisterSeatRequest;
import com.earlybird.ticket.admin.presentation.dto.RegisterVenueRequest;
import com.earlybird.ticket.admin.presentation.dto.UpdateCouponRequest;
import com.earlybird.ticket.admin.presentation.dto.UpdateSeatInstanceRequest;
import com.earlybird.ticket.admin.presentation.dto.UpdateVenueRequest;
import com.earlybird.ticket.common.entity.CommonDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/coupons/issue")
    public CommonDto<Void> issueCoupon(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid RegisterCouponIssueRequest request
    ) {
        adminService.issueCoupon(passport, request.toIssueCouponCommand());
        return CommonDto.created(null, "쿠폰 발행 성공");
    }

    @PostMapping("/coupons")
    public CommonDto<Void> registerCoupon(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid RegisterCouponRequest request
    ) {
        adminService.registerCoupon(passport, request.toRegisterCouponCommand());
        return CommonDto.created(null, "쿠폰 생성 성공");
    }

    @PatchMapping("/coupons")
    public CommonDto<Void> modifyCoupon(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid UpdateCouponRequest request
    ) {
        adminService.modifyCoupon(passport, request.toUpdateCouponCommand());
        return CommonDto.accepted(null, "쿠폰 수정 성공");
    }

    @DeleteMapping("/coupons")
    public CommonDto<Void> withdrawCoupon(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid DeleteCouponRequest request
    ) {
        adminService.withdrawCoupon(passport, request.toDeleteCouponCommand());
        return CommonDto.accepted(null, "쿠폰 삭제 성공");
    }

    @PostMapping("/venues")
    public CommonDto<Void> registerVenue(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid RegisterVenueRequest request
    ) {
        adminService.registerVenue(passport, request.toRegisterVenueCommand());
        return CommonDto.created(null, "공연장 등록 성공");
    }

    @PutMapping("/venues")
    public CommonDto<Void> modifyVenue(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid UpdateVenueRequest request
    ) {
        adminService.modifyVenue(passport, request.toUpdateVenueCommand());
        return CommonDto.accepted(null, "공연장 수정 성공");
    }

    @DeleteMapping("/venues")
    public CommonDto<Void> withdrawVenue(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid DeleteVenueRequest request
    ) {
        adminService.withdrawVenue(passport, request.toDeleteVenueCommand());
        return CommonDto.accepted(null, "공연장 삭제 성공");
    }

    @PostMapping("/seats")
    public CommonDto<Void> registerSeat(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid RegisterSeatRequest request
    ) {
        adminService.registerSeat(passport, request.toRegisterSeatCommand());
        return CommonDto.created(null, "좌석 등록 성공");
    }

    @PutMapping("/seats")
    public CommonDto<Void> modifySeatInstance(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid UpdateSeatInstanceRequest request
    ) {
        adminService.modifySeatInstance(passport, request.toUpdateSeatCommand());
        return CommonDto.accepted(null, "좌석 수정 성공");
    }

    @DeleteMapping("/seats")
    public CommonDto<Void> withdrawSeatInstance(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid DeleteSeatInstanceRequest request
    ) {
        adminService.withdrawSeatInstance(passport, request.toDeleteSeatCommand());
        return CommonDto.accepted(null, "좌석 삭제 성공");
    }
}
