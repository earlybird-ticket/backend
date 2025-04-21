package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptSuccessPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreemptSeatSuccessPayloadHandler implements EventHandler<SeatPreemptSuccessPayload> {

    private final ReservationSeatRepository reservationSeatRepository;
    private final RedissonClient redissonClient;

    @Override
    @Transactional
    public void handle(Event<SeatPreemptSuccessPayload> event) {
        SeatPreemptSuccessPayload payload = event.getPayload();
        Long userId = payload.passportDto()
                             .getUserId();

        log.info("payload = {} ",
                 payload);

        List<ReservationSeat> seatInstanceList = reservationSeatRepository.findAllBySeatInstaceIdIn(payload.seatInstanceIdList());
        log.info("Received UUID List: {}",
                 payload.seatInstanceIdList());
        Reservation reservation = seatInstanceList.get(0)
                                                  .getReservation();

        reservation.updateStatusPaying(userId);
        log.info("Reservation Status ={}",
                 reservation.getReservationStatus());

        seatInstanceList.forEach(seat -> {
            seat.updateStatusPreempted(userId);
            log.info("ReservationStatus Status = {}",
                     seat.getStatus());
        });

    }

    @Override
    public boolean support(Event<SeatPreemptSuccessPayload> event) {
        return event.getEventType() == EventType.SEAT_PREEMPT_SUCCESS;
    }
}