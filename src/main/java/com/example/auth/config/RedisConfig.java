package com.example.auth.config;

import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public RedisConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // Helper class that simplifies Redis data access code.
        // Performs automatic serialization/deserialization between the given objects and the underlying binary data in the Redis store. By default, it uses Java serialization for its objects
        RedisTemplate<String, UserRequestDtoFromKfaka> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory); // Sets the factory used to acquire connections and perform operations on the connected Redis instance.

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        return template;
    }
}
