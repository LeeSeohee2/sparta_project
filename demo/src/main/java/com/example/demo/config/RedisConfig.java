package com.example.demo.config;                         // 설정 파일들이 모여 있는 패키지

import org.springframework.beans.factory.annotation.Value; // application.yml 에서 값 주입을 위해 import
import org.springframework.context.annotation.Bean;        // @Bean 사용을 위한 import
import org.springframework.context.annotation.Configuration; // 설정 클래스 표시용 import
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration                                           // 이 클래스가 스프링 설정 클래스임을 나타냄
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")            // application.yml 의 spring.redis.host 값을 주입, 없으면 localhost 사용
    private String redisHost;                           // Redis 서버 호스트 이름

    @Value("${spring.redis.port:6379}")                 // application.yml 의 spring.redis.port 값을 주입, 없으면 6379 사용
    private int redisPort;                              // Redis 서버 포트 번호

    @Bean                                              // RedisConnectionFactory 를 스프링 빈으로 등록
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort); // Lettuce 드라이버를 사용해서 Redis 연결 생성
    }

    @Bean                                              // StringRedisTemplate 을 스프링 빈으로 등록
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory); // 주입 받은 connectionFactory 로 템플릿 생성
    }
}
