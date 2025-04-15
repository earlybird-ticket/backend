package com.earlybird.ticket.payment.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.redisson.Redisson;
import org.redisson.api.EvictionMode;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedissonConfig {

    private static final String REDIS_PREFIX = "redis://%s:%d";

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress(String.format(REDIS_PREFIX, redisHost, redisPort));
        return Redisson.create(config);
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> cacheConfigMap = new HashMap<>();

        CacheConfig cacheConfig = new CacheConfig(
            TimeUnit.MINUTES.toMillis(10),
            TimeUnit.MINUTES.toMillis(10)
        );
        cacheConfig.setEvictionMode(EvictionMode.LFU);

        cacheConfigMap.put("payment", cacheConfig);
        return new RedissonSpringCacheManager(redissonClient, cacheConfigMap);
    }
}
