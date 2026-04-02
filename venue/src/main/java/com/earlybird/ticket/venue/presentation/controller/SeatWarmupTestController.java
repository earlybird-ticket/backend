package com.earlybird.ticket.venue.presentation.controller;

import com.earlybird.ticket.venue.application.service.SeatService;
import com.earlybird.ticket.venue.presentation.dto.request.WarmUpSeatsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/test/warmup")
@Profile({"loadtest"})
@RequiredArgsConstructor
public class SeatWarmupTestController {

    private final SeatService seatService;


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void warmup(@RequestBody WarmUpSeatsRequest warmUpSeatsRequest) {
        seatService.warmUpSeatInstance(warmUpSeatsRequest.toWarmupSeatCommand());
    }
}
