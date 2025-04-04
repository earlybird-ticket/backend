package com.earlybirdticket.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.earlybirdticket.common.entity.PassportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PassportUtil {

    public PassportDto getPassportDto(String passport) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(passport, PassportDto.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}