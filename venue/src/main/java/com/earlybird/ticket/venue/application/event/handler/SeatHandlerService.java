package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.*;
import org.springframework.stereotype.Service;

@Service
public interface SeatHandlerService {
    void createSeat(SeatCreatePayload seatCreatePayload);

    void createSeatInstance(SeatInstanceCreatePayload seatInstanceCreatePayload);

    void updateSeatInstance(SeatInstanceUpdatePayload seatInstanceUpdatePayload);

    void deleteSeatInstance(SeatInstanceDeletePayload seatInstanceDeletePayload);

    void preemptSeat(SeatPreemptPayload seatPreemptPayload);

    void confirmSeat(SeatConfirmPayload seatConfirmPayload);

    void returnSeat(SeatReturnPayload seatReturnPayload);
}
