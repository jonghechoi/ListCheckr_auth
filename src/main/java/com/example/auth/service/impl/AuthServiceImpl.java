package com.example.auth.service.impl;

import com.example.auth.domain.dto.IdAndPassword;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class AuthServiceImpl {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, UserRequestDtoFromKfaka> redisTemplate;
    private final ValueOperations<String, UserRequestDtoFromKfaka> valueOperations;

    AuthServiceImpl(RedisTemplate<String, UserRequestDtoFromKfaka> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = this.redisTemplate.opsForValue();
    }

    // RedisTemplate 사용
    public boolean save(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        if(valueOperations.get(userRequestDtoFromKfaka.getUid()) != null) {
            return false;
        }

        valueOperations.set(userRequestDtoFromKfaka.getUid(), userRequestDtoFromKfaka);
        return true;
    }

    public boolean getLoginByIdAndPassword(IdAndPassword idAndPassword) {
        Object rawValue = valueOperations.get(idAndPassword.getUid());

        LinkedHashMap<String, Object> valueMap = (LinkedHashMap<String, Object>) rawValue;
        UserRequestDtoFromKfaka map = objectMapper.convertValue(valueMap, UserRequestDtoFromKfaka.class);

        return idAndPassword.getUid() == map.getUid() || idAndPassword.getPassword() == map.getPassword();
    }
}