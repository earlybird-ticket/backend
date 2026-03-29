package com.earlybird.ticket.venue.presentation.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.earlybird.ticket.venue.application.dto.response.SeatListQueryV2;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import com.earlybird.ticket.venue.application.dto.response.SectionListQuery.SectionQuery;
import com.earlybird.ticket.venue.application.service.SeatService;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = SeatController.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private SeatService seatService;

    @Test
    void 섹션_조회_응답에_layoutUrl과_schemaVersion을_포함한다() throws Exception {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        List<SectionQuery> sectionQueries = List.of(
            SectionQuery.builder()
                .section(Section.A.getValue())
                .remainingNumberOfSeats(100)
                .floor(1)
                .grade(Grade.R.getValue())
                .price(BigDecimal.valueOf(10_000))
                .cdnUrl("test-cdn-url-A")
                .schemaVersion("seat-layout-v1")
                .build(),
            SectionQuery.builder()
                .section(Section.B.getValue())
                .remainingNumberOfSeats(100)
                .floor(1)
                .grade(Grade.S.getValue())
                .price(BigDecimal.valueOf(12_000))
                .cdnUrl("test-cdn-url-B")
                .schemaVersion("seat-layout-v1")
                .build()
        );

        SectionListQuery sectionListQuery = SectionListQuery.builder()
            .concertId(concertId)
            .concertSequenceId(concertSequenceId)
            .sectionList(sectionQueries)
            .build();

        BDDMockito.given(seatService.findSectionList(concertSequenceId))
            .willReturn(sectionListQuery);

        // when & then
        mvc.perform(MockMvcRequestBuilders.get(
                    "/api/v1/external/seats/{concert_sequence_id}", concertSequenceId
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.concert_id").value(concertId.toString()))
            .andExpect(jsonPath("$.data.concert_sequence_id").value(concertSequenceId.toString()))
            .andExpect(jsonPath("$.data.section_list[0].section").value("A"))
            .andExpect(jsonPath("$.data.section_list[0].cdn_url").value("test-cdn-url-A"))
            .andExpect(jsonPath("$.data.section_list[0].schema_version").value("seat-layout-v1"))
            .andExpect(jsonPath("$.data.section_list[1].section").value("B"))
            .andExpect(jsonPath("$.data.section_list[1].cdn_url").value("test-cdn-url-B"))
            .andExpect(jsonPath("$.data.section_list[1].schema_version").value("seat-layout-v1"));

    }

    @Test
    void 좌석_조회_응답에_가능한_좌석_인덱스_목록을_반환한다() throws Exception {
        // given
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        SeatListQueryV2 responseV2 = SeatListQueryV2.builder()
            .section(section.getValue())
            .availableIndexes(List.of(13, 1, 3, 5))
            .build();

        BDDMockito.given(seatService.findSeatList(concertSequenceId, section.getValue()))
            .willReturn(responseV2);

        // when & then
        mvc.perform(MockMvcRequestBuilders.get(
                    "/api/v1/external/seats/{concert_sequence_id}/sections/{section}",
                    concertSequenceId, section.getValue()
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.section").value(section.getValue()))
            .andExpect(jsonPath("$.data.available_indexes.length()").value(4))
            .andExpect(jsonPath("$.data.available_indexes[0]").value(13))
            .andExpect(jsonPath("$.data.available_indexes[1]").value(1))
            .andExpect(jsonPath("$.data.available_indexes[2]").value(3))
            .andExpect(jsonPath("$.data.available_indexes[3]").value(5))
            .andExpect(jsonPath("$.data.seat_list").doesNotExist())
            .andExpect(jsonPath("$.data.grade").doesNotExist())
            .andExpect(jsonPath("$.data.floor").doesNotExist());
    }


}