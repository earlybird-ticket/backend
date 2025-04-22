package com.earlybird.ticket.alarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AlarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlarmApplication.class,
                              args);
    }

}
