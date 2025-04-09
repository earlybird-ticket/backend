package com.earlybird.ticket.user.presentation.dto.response;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.user.application.dto.query.FindUserQuery;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record FindUserInfoResponse(
    Long userId,
    String email,
    String name,
    Role role,
    LocalDate birthDay,
    String address,
    String phoneNumber,
    @JsonInclude(Include.NON_NULL) String businessNumber
) {

    public static FindUserInfoResponse of(FindUserQuery findUserQuery) {
        return FindUserInfoResponse.builder()
            .userId(findUserQuery.userId())
            .email(findUserQuery.email())
            .name(findUserQuery.name())
            .role(findUserQuery.role())
            .birthDay(findUserQuery.birthDay())
            .address(findUserQuery.address())
            .phoneNumber(findUserQuery.phoneNumber())
            .businessNumber(findUserQuery.businessNumber())
            .build();
    }
}
