package com.earlybird.ticket.user.presentation.dto.response;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.user.application.dto.query.GetUserIdPasswordRoleQuery;
import lombok.Builder;

@Builder
public record GetUserEmailPasswordRoleResponse(
    Long userId,
    String password,
    Role role
) {

    public static GetUserEmailPasswordRoleResponse of(GetUserIdPasswordRoleQuery query) {
        return GetUserEmailPasswordRoleResponse.builder()
            .userId(query.userId())
            .password(query.password())
            .role(query.role())
            .build();
    }
}
