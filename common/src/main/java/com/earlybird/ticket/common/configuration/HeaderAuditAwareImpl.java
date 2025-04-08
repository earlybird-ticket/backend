package com.earlybird.ticket.common.configuration;

import com.earlybird.ticket.common.util.PassportUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class HeaderAuditAwareImpl implements AuditorAware<Long> {

    private final PassportUtil passportUtil;
    public static final String PASSPORT_HEADER = "X-User-Passport";

    @Override
    public Optional<Long> getCurrentAuditor() {
        // 전역 Request유틸 클래스에서 Request -> ServletRequest로 변환
        // -> RequestHeader -> Passport -> userId
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(ServletRequestAttributes.class::isInstance)
            .map(ServletRequestAttributes.class::cast)
            .map(ServletRequestAttributes::getRequest)
            .map(request -> request.getHeader(PASSPORT_HEADER))
            .map(passportUtil::getPassportDto)
            .flatMap(passportDto -> Optional.of(passportDto.getUserId()));
    }
}
