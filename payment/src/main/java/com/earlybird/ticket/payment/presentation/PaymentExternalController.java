package com.earlybird.ticket.payment.presentation;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.payment.application.service.PaymentService;
import com.earlybird.ticket.payment.application.service.dto.query.FindPaymentQuery;
import com.earlybird.ticket.payment.presentation.dto.request.CreatePaymentRequest;
import com.earlybird.ticket.payment.presentation.dto.request.FindPaymentResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/payments")
@RequiredArgsConstructor
public class PaymentExternalController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonDto<Void>> createPayment(
        @RequestBody CreatePaymentRequest paymentRequest
    ) {
        UUID paymentId = paymentService.createPayment(paymentRequest.toCreatePaymentCommand());
        return ResponseEntity.ok(CommonDto.ok(null, "결제 생성 성공"));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<CommonDto<FindPaymentResponse>> findPayment(
        @PathVariable(name = "paymentId") UUID paymentId
    ) {
        FindPaymentQuery payment = paymentService.findPayment(paymentId);
        return ResponseEntity.ok(CommonDto.ok(FindPaymentResponse.of(payment), "조회 성공"));
    }
}
