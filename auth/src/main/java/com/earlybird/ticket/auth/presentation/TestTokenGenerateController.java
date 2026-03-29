package com.earlybird.ticket.auth.presentation;

import com.earlybird.ticket.auth.application.TokenProvider;
import com.earlybird.ticket.auth.presentation.dto.response.GenerateTokenResponse;
import java.util.List;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("loadtest")
@RequestMapping("/test/generate-tokens")
@RequiredArgsConstructor
public class TestTokenGenerateController {

    private final TokenProvider tokenProvider;

    @PostMapping
    public List<GenerateTokenResponse> generateTokens(
        @RequestParam("start") int start,
        @RequestParam("count") int count
    ) {
        return LongStream.range(start, start + count)
            .mapToObj(
                i -> GenerateTokenResponse.from(
                    i,
                    tokenProvider.generateAccessToken(i, "USER")
                )
            )
            .toList();
    }
}
