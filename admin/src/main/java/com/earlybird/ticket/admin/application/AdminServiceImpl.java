package com.earlybird.ticket.admin.application;

import com.earlybird.ticket.admin.application.dto.DeleteCouponCommand;
import com.earlybird.ticket.admin.application.dto.DeleteSeatInstanceCommand;
import com.earlybird.ticket.admin.application.dto.DeleteVenueCommand;
import com.earlybird.ticket.admin.application.dto.IssueCouponCommand;
import com.earlybird.ticket.admin.application.dto.RegisterCouponCommand;
import com.earlybird.ticket.admin.application.dto.RegisterSeatCommand;
import com.earlybird.ticket.admin.application.dto.RegisterVenueCommand;
import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import com.earlybird.ticket.admin.application.dto.UpdateSeatInstanceCommand;
import com.earlybird.ticket.admin.application.dto.UpdateVenueCommand;
import com.earlybird.ticket.admin.application.event.Event;
import com.earlybird.ticket.admin.application.event.EventType;
import com.earlybird.ticket.admin.application.event.dto.CouponCreateEvent;
import com.earlybird.ticket.admin.application.event.dto.CouponDeleteEvent;
import com.earlybird.ticket.admin.application.event.dto.CouponUpdateEvent;
import com.earlybird.ticket.admin.application.event.dto.SeatCreateEvent;
import com.earlybird.ticket.admin.application.event.dto.SeatInstanceDeleteEvent;
import com.earlybird.ticket.admin.application.event.dto.SeatInstanceUpdateEvent;
import com.earlybird.ticket.admin.application.event.dto.VenueCreateEvent;
import com.earlybird.ticket.admin.application.event.dto.VenueDeleteEvent;
import com.earlybird.ticket.admin.application.event.dto.VenueUpdateEvent;
import com.earlybird.ticket.admin.common.Outbox;
import com.earlybird.ticket.admin.common.OutboxRepository;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final PassportUtil passportUtil;
    private final OutboxRepository outboxRepository;

    @Transactional
    @Override
    public void issueCoupon(String passport, IssueCouponCommand command) {
        // TODO : 조건에 맞는 사용자에게 coupon 발행 로직 구현 예정
    }

    @Transactional
    @Override
    public void registerCoupon(String passport, RegisterCouponCommand command) {
        log.info("passport={}", passport);
        log.info(
                "command = couponName={}, discountRate={}, couponType={}", command.couponName(),
                command.discountRate(), command.couponType()
        );
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.CREATE_COUPON, CouponCreateEvent.toPayload(passportDto, command),
                Outbox.AggregateType.COUPON
        );
    }

    @Transactional
    @Override
    public void modifyCoupon(String passport, UpdateCouponCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.UPDATE_COUPON, CouponUpdateEvent.toPayload(passportDto, command),
                Outbox.AggregateType.COUPON
        );
    }

    @Transactional
    @Override
    public void withdrawCoupon(String passport, DeleteCouponCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.DELETE_COUPON, CouponDeleteEvent.toPayload(passportDto, command),
                Outbox.AggregateType.COUPON
        );
    }

    @Transactional
    @Override
    public void registerVenue(String passport, RegisterVenueCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.CREATE_VENUE, VenueCreateEvent.toPayload(passportDto, command),
                Outbox.AggregateType.VENUE
        );
    }

    @Transactional
    @Override
    public void modifyVenue(String passport, UpdateVenueCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.UPDATE_VENUE, VenueUpdateEvent.toPayload(passportDto, command),
                Outbox.AggregateType.VENUE
        );
    }

    @Transactional
    @Override
    public void withdrawVenue(String passport, DeleteVenueCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.DELETE_VENUE, VenueDeleteEvent.toPayload(passportDto, command),
                Outbox.AggregateType.VENUE
        );
    }

    @Transactional
    @Override
    public void modifySeatInstance(
            String passport, UpdateSeatInstanceCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.UPDATE_SEAT_INSTANCE,
                SeatInstanceUpdateEvent.toPayload(passportDto, command),
                Outbox.AggregateType.SEAT
        );
    }

    @Transactional
    @Override
    public void registerSeat(String passport, RegisterSeatCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.CREATE_SEAT,
                SeatCreateEvent.toPayload(passportDto, command),
                Outbox.AggregateType.SEAT
        );
    }

    @Transactional
    @Override
    public void withdrawSeatInstance(
            String passport, DeleteSeatInstanceCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        saveOutbox(
                EventType.DELETE_SEAT_INSTANCE,
                SeatInstanceDeleteEvent.toPayload(passportDto, command),
                Outbox.AggregateType.SEAT
        );
    }

    private void saveOutbox(EventType eventType, EventPayload payloadData, String aggregateType) {
        log.info(
                "eventType={}, payloadData={}, aggregateType={}", eventType, payloadData,
                aggregateType
        );
        Outbox outbox = Outbox.builder()
                .aggregateId(UUID.randomUUID())
                .eventType(eventType)
                .payload(Event.of(
                        eventType,
                        payloadData,
                        LocalDateTime.now().toString()
                ).toJson())
                .aggregateType(aggregateType)
                .build();
        log.info("outbox= {}", outbox.getPayload());
        outboxRepository.save(outbox);
    }
}
