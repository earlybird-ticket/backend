package com.earlybird.ticket.user.application.dto.query;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.user.domain.entity.User;
import lombok.Builder;

@Builder
public record GetUserIdPasswordRoleQuery(
    Long userId,
    String password,
    Role role
) {

    public static GetUserIdPasswordRoleQuery of(User user) {
        return GetUserIdPasswordRoleQuery.builder()
            .userId(user.getId())
            .password(user.getPassword())
            .role(user.getRole())
            .build();
    }
}
