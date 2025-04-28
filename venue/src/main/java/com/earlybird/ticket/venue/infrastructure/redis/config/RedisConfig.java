package com.earlybird.ticket.venue.infrastructure.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
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
        config.setCodec(new StringCodec());
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
                  redis.call('HSET', KEYS[i], 'status', 'PREEMPTED')
                  redis.call('HSET', KEYS[i], 'userId', ARGV[1])
                  redis.call('HSET', KEYS[i], 'reservationId', ARGV[2])
                  redis.call('HSET', KEYS[i], 'updatedAt', ARGV[4])
                end
               
                -- 3. Section List remaining Seat 감소
                for i = 1, #KEYS do
                   local key = KEYS[i];
                   local concertId = redis.call('HGET', key, 'concertId')
                   local section = redis.call('HGET', key, 'section')
                   local concertSequenceId = string.match(key, '^SEAT_INSTANCE:([^:]+):')
                   redis.call('HINCRBY', 'SECTION_LIST:' .. concertId .. ':' .. concertSequenceId .. ':' ..section, 'remainingSeat', -1)
                end
               
                -- 5. 예약 ID TTL 설정
                return redis.call('SET', 'TIME_LIMIT:RESERVATION_ID:' .. ARGV[2], 'PREEMPTED', 'NX', 'PX', tonumber(ARGV[3]))
               
               """);
        redisScript.setResultType(Object.class);
        return redisScript;
    }

    @Bean
    public RedisScript<Object> seatPreemptByVIPScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText("""
                -- 1. 상태 확인 및 예약 불가면 ZSET에 등록
                for i = 1, #KEYS do
                  local status = redis.call('HGET', KEYS[i], 'status')
                  local userId = redis.call('HGET', KEYS[i], 'userId')
               
                  if status ~= 'FREE' then
                    local concertSequenceId = string.match(KEYS[i], '^SEAT_INSTANCE:([^:]+):')
                    local seatInstanceId = string.match(KEYS[i], '.+:([^:]+)$')
                    redis.call('ZADD', 'VIP_QUEUE:' .. concertSequenceId, ARGV[4],'userId:' .. ARGV[1] .. ':seatInstanceId:' .. seatInstanceId)
                    return 0
                  end
                end
               
                -- 2. 상태 갱신
                for i = 1, #KEYS do
                  redis.call('HSET', KEYS[i], 'status', 'PREEMPTED')
                  redis.call('HSET', KEYS[i], 'userId', ARGV[1])
                  redis.call('HSET', KEYS[i], 'reservationId', ARGV[2])
                  redis.call('HSET', KEYS[i], 'updatedAt', ARGV[4])
                end
               
                -- 3. Section List remaining Seat 감소
                for i = 1, #KEYS do
                   local key = KEYS[i];
                   local concertId = redis.call('HGET', key, 'concertId')
                   local section = redis.call('HGET', key, 'section')
                   local concertSequenceId = string.match(key, '^SEAT_INSTANCE:([^:]+):')
                   redis.call('HINCRBY', 'SECTION_LIST:' .. concertId .. ':' .. concertSequenceId .. ':' ..section, 'remainingSeat', -1)
                end
               
                -- 5. 예약 ID TTL 설정
                return redis.call('SET', 'TIME_LIMIT:RESERVATION_ID:' .. ARGV[2], 'PREEMPTED', 'NX', 'PX', tonumber(ARGV[3]))
               
               """);
        redisScript.setResultType(Object.class);
        return redisScript;
    }

    @Bean
    public RedisScript<Object> waitingSeatPreemptByVIPScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText("""
                -- 1. 상태 확인
                for i = 1, #KEYS do
                  local status = redis.call('HGET', KEYS[i], 'status')
                  local userId = redis.call('HGET', KEYS[i], 'userId')
               
                  if not (status == 'WAIT' and userId == ARGV[1]) then
                    return 0
                  end
                end
               
                -- 2. 상태 갱신
                for i = 1, #KEYS do
                  redis.call('HSET', KEYS[i], 'status', 'PREEMPTED')
                  redis.call('HSET', KEYS[i], 'reservationId', ARGV[2])
                  redis.call('HSET', KEYS[i], 'updatedAt', ARGV[4])
                end
               
                -- 3. 예약 ID TTL 설정
                redis.call('SET', 'TIME_LIMIT:RESERVATION_ID:' .. ARGV[2], 'PREEMPTED', 'NX', 'PX', tonumber(ARGV[3]))
               
               -- 4. Waiting 키 삭제
                for i = 1, #KEYS do
                   local seatInstanceId = string.match(KEYS[i], '.+:([^:]+)$')
                   local concertSequenceId = string.match(KEYS[i], '^SEAT_INSTANCE:([^:]+):')
                   redis.call('DEL', 'TIME_LIMIT:VIP:' .. concertSequenceId .. ':' .. seatInstanceId)
                end
               
                return 0
               """);
        redisScript.setResultType(Object.class);
        return redisScript;
    }

    @Bean
    public RedisScript<Object> seatCheckScript() {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText("""
                for i = 1, #KEYS do
                  local status = redis.call('HGET', KEYS[i], 'status')
                  if status ~= 'FREE' then
                    return 0
                  end
                end
                
                return 1
                """);
        redisScript.setResultType(Object.class);
        return redisScript;
    }

    @Bean
    public RedisScript<Long> seatReturnScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText("""
                -- 1. 상태 확인
                for i = 1, #KEYS do
                  local status = redis.call('HGET', KEYS[i], 'status')
                  local userId = redis.call('HGET', KEYS[i], 'userId')
                  local reservationId = redis.call('HGET', KEYS[i], 'reservationId')
                
                  if status == 'FREE' or userId ~= ARGV[1] or reservationId ~= ARGV[2] then
                    return 0
                  end
                end
                
                -- 2. 상태 갱신
                for i = 1, #KEYS do
                  redis.call('HSET', KEYS[i], 'status', 'FREE')
                  redis.call('HSET', KEYS[i], 'userId', '')
                  redis.call('HSET', KEYS[i], 'reservationId', '')
                  redis.call('HSET', KEYS[i], 'updatedAt', ARGV[3])
                end
                
                -- 3. 남은 좌석 수 복구
                for i = 1, #KEYS do
                   local key = KEYS[i];
                   local concertId = redis.call('HGET', key, 'concertId')
                   local section = redis.call('HGET', key, 'section')
                   local concertSequenceId = string.match(key, '^SEAT_INSTANCE:([^:]+):')
                   redis.call('HINCRBY', 'SECTION_LIST:' .. concertId .. ':' .. concertSequenceId .. ':' ..section, 'remainingSeat', 1)
                end
                
                -- 4. TTL 삭제
                redis.call('DEL', 'TIME_LIMIT:RESERVATION_ID:' .. ARGV[2])
                
                return 1
                """);

        redisScript.setResultType(Long.class);
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
