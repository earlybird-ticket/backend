package com.earlybird.ticket.concert.application;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.concert.application.dto.DeleteConcertCommand;
import com.earlybird.ticket.concert.application.dto.DeleteConcertSequenceCommand;
import com.earlybird.ticket.concert.application.dto.ProcessConcertDetailsQuery;
import com.earlybird.ticket.concert.application.dto.ProcessConcertQuery;
import com.earlybird.ticket.concert.application.dto.RegisterConcertCommand;
import com.earlybird.ticket.concert.application.dto.UpdateConcertCommand;
import com.earlybird.ticket.concert.application.dto.UpdateConcertSequenceCommand;
import com.earlybird.ticket.concert.application.event.Event;
import com.earlybird.ticket.concert.application.event.EventType;
import com.earlybird.ticket.concert.application.event.dto.ConcertCreateSuccessEvent;
import com.earlybird.ticket.concert.common.Outbox;
import com.earlybird.ticket.concert.common.OutboxRepository;
import com.earlybird.ticket.concert.domain.Concert;
import com.earlybird.ticket.concert.domain.ConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final OutboxRepository outboxRepository;
    private final PassportUtil passportUtil;

    @Transactional
    @Override
    public void registerConcert(String passport, RegisterConcertCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);

        Concert concert = command.toEntity();

        concertRepository.save(concert);

        Outbox outbox = Outbox.builder()
                .aggregateType(Outbox.AggregateType.CONCERT)
                .aggregateId(concert.getConcertId())
                .eventType(EventType.SEAT_INSTANCE_CREATE)
                .payload(Event.of(
                        EventType.SEAT_INSTANCE_CREATE,
                        ConcertCreateSuccessEvent.toPayload(passportDto, concert),
                        LocalDateTime.now().toString()
                ).toJson())
                .build();

        outboxRepository.save(outbox);
    }

    @Transactional
    @Override
    public void modifyConcert(String passport, UpdateConcertCommand command) {

        Concert concert = concertRepository.findByConcertId(command.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));

        concert.updateConcert(
                command.concertName(),
                command.entertainerName(),
                command.startDate(),
                command.endDate(),
                command.genre(),
                command.runningTime(),
                command.priceInfo()
        );
    }

    @Transactional
    @Override
    public void deleteConcert(String passport, DeleteConcertCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);

        Concert concert = concertRepository.findByConcertId(command.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));

        concert.deleteConcert(passportDto.getUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public ProcessConcertDetailsQuery processConcertDetail(
            String passport, UUID concertId
    ) {
        Concert concert = concertRepository.findByConcertId(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));

        return ProcessConcertDetailsQuery.of(concert.getConcertSequences());
    }

    // 콘서트는 범위 및 조건 검색이 가능하다
    @Transactional(readOnly = true)
    @Override
    public Page<ProcessConcertQuery> search(
            String passport, Integer page, Integer size, String sort, String q, String orderBy) {

        Sort.Direction direction = sort.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, orderBy));

        Page<Concert> concertPage = concertRepository.searchConcert(q, sort, orderBy, pageRequest);

        // concertPage → ProcessConcertQuery로 매핑
        Page<ProcessConcertQuery> resultPage = concertPage.map(ProcessConcertQuery::of
        );

        return resultPage;
    }


    @Transactional
    @Override
    public void modifyConcertSequence(String passport, UpdateConcertSequenceCommand command) {
        Concert concert = concertRepository.findByConcertId(command.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));

        concert.updateConcertSequenceBuilder()
                .concertSequenceId(command.concertSequenceId())
                .sequenceStartDate(command.sequenceStartDate())
                .sequenceEndDate(command.sequenceEndDate())
                .ticketSaleStartDate(command.ticketSaleStartDate())
                .ticketSaleEndDate(command.ticketSaleEndDate())
                .status(command.concertStatus())
                .build();
    }

    @Transactional
    @Override
    public void deleteConcertSequence(String passport, DeleteConcertSequenceCommand command) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);

        Concert concert = concertRepository.findByConcertId(command.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found"));

        concert.deleteConcertSequence(passportDto.getUserId(), command.concertSequenceId());
    }
}
