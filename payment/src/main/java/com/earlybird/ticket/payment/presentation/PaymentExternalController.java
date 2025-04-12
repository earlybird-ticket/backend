package com.earlybird.ticket.payment.presentation;

import com.earlybird.ticket.payment.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/payments")
@RequiredArgsConstructor
public class PaymentExternalController {

    private final PaymentService paymentService;

}
