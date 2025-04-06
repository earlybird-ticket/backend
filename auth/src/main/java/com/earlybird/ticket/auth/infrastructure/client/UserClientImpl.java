package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.auth.application.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {
    private final UserFeignClient userFeignClient;

    //TODO:: client를 사용한 구현은 여기서, dto는 payload로 사용
    // application에는 commander로 반환
}
