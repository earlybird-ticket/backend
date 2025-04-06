package com.earlybird.ticket.auth.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service")
public interface UserFeignClient {

}
