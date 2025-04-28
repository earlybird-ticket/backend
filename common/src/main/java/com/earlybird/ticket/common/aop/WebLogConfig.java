package com.earlybird.ticket.common.aop;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebLogConfig {

    @Bean
    public FilterRegistrationBean<CachingMdcFilter> cachingMdcFilter() {
        FilterRegistrationBean<CachingMdcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingMdcFilter());

        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.setName("cachingMdcFilter");
        registrationBean.addUrlPatterns("/*");


        return registrationBean;
    }
}
