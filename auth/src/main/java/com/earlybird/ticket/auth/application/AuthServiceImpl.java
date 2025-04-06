package com.earlybird.ticket.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient;

    //TODO:: 수행할 dto는 commander로 가져와서 수행
}
