package com.earlybirdticket.common.aop;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebLogConfig {

    @Bean
    public FilterRegistrationBean<CachingMdcFilter> cachingMdcFilter() {
        FilterRegistrationBean<CachingMdcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingMdcFilter());
        registrationBean.setOrder(1);
        registrationBean.setName("cachingMdcFilter");
        registrationBean.addUrlPatterns(("/api/*"));
        return registrationBean;
    }
}
