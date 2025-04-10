package com.earlybird.ticket.admin.presentation;

import com.earlybird.ticket.admin.application.AdminService;
import com.earlybird.ticket.admin.presentation.dto.DeleteCouponRequest;
import com.earlybird.ticket.admin.presentation.dto.DeleteVenueRequest;
import com.earlybird.ticket.admin.presentation.dto.RegisterCouponIssueRequest;
import com.earlybird.ticket.admin.presentation.dto.RegisterVenueRequest;
import com.earlybird.ticket.admin.presentation.dto.UpdateCouponRequest;
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

    @PostMapping("/coupon")
    public CommonDto<Void> issueCoupon(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid RegisterCouponIssueRequest request
    ) {
        adminService.issueCoupon(request.toIssueCouponCommand());
        return CommonDto.ok(null, "쿠폰 발행 성공");
    }

    @PatchMapping("/coupon")
    public CommonDto<Void> modifyCoupon(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid UpdateCouponRequest request
    ) {
        adminService.modifyCoupon(request.toUpdateCouponCommand());
        return CommonDto.ok(null, "쿠폰 수정 성공");
    }

    @DeleteMapping("/coupon")
    public CommonDto<Void> withdrawCoupon(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid DeleteCouponRequest request
    ) {
        adminService.withdrawCoupon(request.toDeleteCouponCommand());
        return CommonDto.ok(null, "쿠폰 삭제 성공");
    }

    @PostMapping("/venue")
    public CommonDto<Void> registerVenue(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid RegisterVenueRequest request
    ) {
        adminService.registerVenue(request.toRegisterVenueCommand());
        return CommonDto.ok(null, "공연장 등록 성공");
    }

    @PutMapping("/venue")
    public CommonDto<Void> modifyVenue(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid UpdateVenueRequest request
    ) {
        adminService.modifyVenue(request.toUpdateVenueCommand());
        return CommonDto.ok(null, "공연장 수정 성공");
    }

    @DeleteMapping("/venue")
    public CommonDto<Void> withdrawVenue(
            @RequestHeader(value = "X-User-Passport") String passport,
            @RequestBody @Valid DeleteVenueRequest request
    ) {
        adminService.withdrawVenue(request.toDeleteVenueCommand());
        return CommonDto.ok(null, "공연장 삽입 성공");
    }
}
