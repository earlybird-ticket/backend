package com.earlybird.ticket.concert.application;

import com.earlybird.ticket.concert.application.dto.DeleteConcertCommand;
import com.earlybird.ticket.concert.application.dto.DeleteConcertSequenceCommand;
import com.earlybird.ticket.concert.application.dto.ProcessConcertDetailsCommand;
import com.earlybird.ticket.concert.application.dto.ProcessConcertDetailsQuery;
import com.earlybird.ticket.concert.application.dto.ProcessConcertQuery;
import com.earlybird.ticket.concert.application.dto.ProcessConcertsCommand;
import com.earlybird.ticket.concert.application.dto.RegisterConcertCommand;
import com.earlybird.ticket.concert.application.dto.UpdateConcertCommand;
import com.earlybird.ticket.concert.application.dto.UpdateConcertSequenceCommand;

public interface ConcertService {


    void registerConcert(String passport, RegisterConcertCommand command);

    void modifyConcert(String passport, UpdateConcertCommand command);

    void deleteConcert(String passport, DeleteConcertCommand command);

    ProcessConcertDetailsQuery processConcertDetail(
            String passport, ProcessConcertDetailsCommand command);

    ProcessConcertQuery processConcerts(String passport, ProcessConcertsCommand command);

    void modifyConcertSequence(String passport, UpdateConcertSequenceCommand command);

    void deleteConcertSequence(String passport, DeleteConcertSequenceCommand command);
}
