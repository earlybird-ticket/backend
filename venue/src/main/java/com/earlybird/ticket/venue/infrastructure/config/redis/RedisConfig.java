package com.earlybird.ticket.venue.infrastructure.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }

    @Bean
    public RedisScript<Object> seatPreemptScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText("""
                -- 1. 상태 확인
                for i = 1, #KEYS do
                  local status = redis.call('HGET', KEYS[i], 'status')
                  if status ~= 'FREE' then
                    return 0
                  end
                end
                
                -- 2. 상태 갱신
                for i = 1, #KEYS do
                  redis.call('HSET', KEYS[i], 'status', 'PREEMPT')
                  redis.call('HSET', KEYS[i], 'userId', ARGV[1])
                  redis.call('HSET', KEYS[i], 'reservationId', ARGV[2])
                  redis.call('HSET', KEYS[i], 'updatedAt', ARGV[4])
                end
                
                -- 3. 예약 ID TTL 설정
                return redis.call('SET', 'TIME_LIMIT:RESERVATION_ID:' .. ARGV[2], 'PREEMPT', 'NX', 'PX', tonumber(ARGV[3]))
                
                """);
        redisScript.setResultType(Object.class);
        return redisScript;
    }

    @Bean
    public RedisTemplate<String, String> scriptRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet(); // 명시적으로 호출
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.afterPropertiesSet(); // 명시적으로 호출
        return template;
    }
}
