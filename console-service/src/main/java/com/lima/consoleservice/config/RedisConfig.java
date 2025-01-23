package com.lima.consoleservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// redisTemplate 를 사용했는데 실행시 bean 등록이 안되어서 config를 만들었따!
@Configuration
public class RedisConfig {


  @Bean
  public RedisTemplate<String, Object> redisTemplate (LettuceConnectionFactory lettuceConnectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(lettuceConnectionFactory); // redis 서버 연결
    redisTemplate.setKeySerializer(new StringRedisSerializer()); // redis에 저장되는 키를 직렬화 한다. 문자열로
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // redis에 저장되는 값을 직렬화 한다. JSON 형태로
    redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // redis에 저장되는 hash의 키를 직렬화 한다.
    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // redis에 저장되는 hash값을 직렬화 한다. JSON으로
    redisTemplate.afterPropertiesSet(); //redisTemplate이 설정된 값을 기반으로 초기화 작업을 수행한다.
    return redisTemplate;
  }
}

