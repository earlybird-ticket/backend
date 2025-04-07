package com.earlybird.ticket.venue.presentation.controller;

import com.earlybird.ticket.venue.application.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;
}
