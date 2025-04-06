package com.earlybird.ticket.payment.presentation;

import com.earlybird.ticket.payment.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

}
