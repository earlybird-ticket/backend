package com.earlybird.ticket.venue.infrastructure.storage.config;

import com.earlybird.ticket.venue.application.service.SeatLayoutArtifactGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArtifactSerializationConfig {

    @Bean
    public SeatLayoutArtifactGenerator seatLayoutArtifactGenerator() {
        ObjectMapper artifactObjectMapper = JsonMapper.builder()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .build();

        return new SeatLayoutArtifactGenerator(artifactObjectMapper);
    }

}
