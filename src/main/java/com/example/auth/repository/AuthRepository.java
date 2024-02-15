package com.example.auth.repository;

import com.example.auth.domain.User;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Slf4j
@Component
public class AuthRepository {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, UserRequestDtoFromKfaka> redisTemplate;
    private final ValueOperations<String, UserRequestDtoFromKfaka> valueOperations;

    AuthRepository(RedisTemplate<String, UserRequestDtoFromKfaka> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = this.redisTemplate.opsForValue();
    }

    public boolean save(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        if(userRequestDtoFromKfaka.getUid() == null) {
            return false;
        }

        if(valueOperations.get(userRequestDtoFromKfaka.getUid()) != null) {
            return false;
        }
        else {
            valueOperations.set(userRequestDtoFromKfaka.getUid(), userRequestDtoFromKfaka);
            return true;
        }
    }

    public boolean isUidExist(String uid) {
        Object userData = valueOperations.get(uid);

        if(userData == null) {
            return false;
        }
        return true;
    }

    public UserRequestDtoFromKfaka getUser(String uid) {
        Object userData = valueOperations.get(uid);
        LinkedHashMap<String, Object> userDataMap = (LinkedHashMap<String, Object>) userData;
        UserRequestDtoFromKfaka map = objectMapper.convertValue(userDataMap, UserRequestDtoFromKfaka.class);

        return map;
    }

    public boolean updateUser(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        if(valueOperations.setIfPresent(userRequestDtoFromKfaka.getUid(), userRequestDtoFromKfaka)) {
            return true;
        }
        return false;
    }
}