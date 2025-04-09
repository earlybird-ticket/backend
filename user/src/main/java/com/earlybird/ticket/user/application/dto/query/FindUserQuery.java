package com.earlybird.ticket.user.application.dto.query;


import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.user.domain.entity.Seller;
import com.earlybird.ticket.user.domain.entity.User;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record FindUserQuery(
    Long userId,
    String email,
    String name,
    Role role,
    LocalDate birthDay,
    String address,
    String phoneNumber,
    @Nullable String businessNumber
) {

    // TODO: 추후 UserQuery를 class로 바꾸고 다형성 활용
    public static FindUserQuery of(User user) {
        return FindUserQuery.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getRole())
            .birthDay(user.getBirthDay())
            .address(user.getAddress())
            .phoneNumber(user.getPhoneNumber())
            .businessNumber(user instanceof Seller ? ((Seller) user).getBusinessNumber() : null)
            .build();
    }
}
