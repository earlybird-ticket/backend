package com.earlybird.ticket.presentation;

import com.earlybird.ticket.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

}
