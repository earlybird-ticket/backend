package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.common.util.EventPayloadConverter;
import com.earlybird.ticket.reservation.domain.dto.request.CreateReservationEvent;
import com.earlybird.ticket.reservation.domain.dto.request.ReservationFailEvent;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Outbox;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.repository.OutboxRepository;
import com.earlybird.ticket.reservation.domain.repository.ReservationRepository;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.earlybird.ticket.reservation.domain.entity.Outbox.AggregateType.RESERVATION;
import static com.earlybird.ticket.reservation.domain.entity.constant.EventType.RESERVATION_CREATE;
import static com.earlybird.ticket.reservation.domain.entity.constant.EventType.RESERVATION_CREATE_FAIL;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateReservationRequestHandler implements EventHandler<CreateReservationEvent> {

    private final ReservationSeatRepository reservationSeatRepository;
    private final ReservationRepository reservationRepository;
    private final EventPayloadConverter eventPayloadConverter;
    private final OutboxRepository outboxRepository;

    private static ReservationSeat createReservationSeat(CreateReservationEvent.SeatRequest seat,
                                                         CreateReservationEvent payload,
                                                         Reservation reservation) {
        return ReservationSeat.create(seat.getSeatInstanceId(),
                                      payload.getConcertId(),
                                      seat.getSeatCol(),
                                      seat.getSeatRow(),
                                      seat.getSeatPrice(),
                                      seat.getSeatGrade(),
                                      reservation);
    }

    private static Outbox createFailEventOutbox(@NotNull(message = "reservationId is Necessary") UUID reservationId,
                                                String serializedFailEventPayload) {
        return Outbox.builder()
                     .aggregateId(reservationId)
                     .aggregateType(RESERVATION)
                     .payload(serializedFailEventPayload)
                     .eventType(RESERVATION_CREATE_FAIL)
                     .build();
    }

    private static Reservation createReservation(CreateReservationEvent payload) {
        return Reservation.createReservation(payload.getPassportDto()
                                                    .getUserId(),
                                             payload.getUserName(),
                                             payload.getConcertId(),
                                             payload.getConcertName(),
                                             payload.getConcertSequenceId(),
                                             payload.getConcertSequenceStartDatetime(),
                                             payload.getConcertSequenceEndDatetime(),
                                             payload.getConcertSequenceStatus(),
                                             payload.getVenueId(),
                                             payload.getVenueArea(),
                                             payload.getVenueLocation(),
                                             payload.getHallId(),
                                             payload.getHallName(),
                                             payload.getHallFloor());
    }

    @Override
    public void handle(Event<CreateReservationEvent> event) {
        CreateReservationEvent payload = event.getPayload();
        List<UUID> instanceSeatIdList = new ArrayList<>();

        int maxRetry = 3;
        int retryCount = 0;

        while (retryCount < maxRetry) {
            try {
                performReservationTransaction(payload,
                                              instanceSeatIdList);
                return;
            } catch (Exception e) {
                retryCount++;
                log.warn("[CreateReservationRequestHandler] 예약 생성 실패 ({}회): {}",
                         retryCount,
                         e.getMessage());

                if (retryCount >= maxRetry) {
                    log.error("[CreateReservationRequestHandler] 최대 재시도 초과, 실패 이벤트 발행");

                    ReservationFailEvent failEvent = ReservationFailEvent.builder()
                                                                         .passportDto(payload.getPassportDto())
                                                                         .seatInstanceIdList(instanceSeatIdList)
                                                                         .code(Code.RESERVATION_CREATE_FAIL.getCode())
                                                                         .build();

                    Event<ReservationFailEvent> failEventWrapper = new Event<>(RESERVATION_CREATE_FAIL,
                                                                               failEvent,
                                                                               LocalDateTime.now()
                                                                                            .toString());

                    String serializedPayload = eventPayloadConverter.serializePayload(failEventWrapper);

                    Outbox outbox = createFailEventOutbox(payload.getReservationId(),
                                                          serializedPayload);
                    outboxRepository.save(outbox);
                }
            }
        }
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void performReservationTransaction(CreateReservationEvent payload,
                                              List<UUID> instanceSeatIdList) {
        Reservation reservation = createReservation(payload);
        reservation = reservationRepository.save(reservation);

        for (CreateReservationEvent.SeatRequest seat : payload.getSeatList()) {
            instanceSeatIdList.add(seat.getSeatInstanceId());
            ReservationSeat reservationSeat = createReservationSeat(seat,
                                                                    payload,
                                                                    reservation);
            reservationSeatRepository.save(reservationSeat);
        }
    }

    @Override
    public boolean support(Event<CreateReservationEvent> event) {
        return event.getEventType() == RESERVATION_CREATE;
    }
}