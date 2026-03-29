package com.earlybird.ticket.auth.presentation.dto.response;


public record GenerateTokenResponse(
    Long userId,
    String email,
    String name,
    String token
) {

    public static GenerateTokenResponse from(long number, String token) {
        return new GenerateTokenResponse(
            number,
            "tester" + number + "@test.com",
            "tester" + number,
            token
        );
    }

}
