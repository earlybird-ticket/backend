package com.earlybird.ticket.payment.presentation;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api/v1/external/payments")
public class PaymentViewController {

    @GetMapping("/checkout")
    public String checkout(@RequestParam(name = "paymentId") UUID paymentId) {
        return "checkout";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/fail")
    public String fail() {
        return "fail";
    }
}
