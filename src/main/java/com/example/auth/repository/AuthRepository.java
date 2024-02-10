package com.example.auth.repository;

import com.example.auth.domain.dto.IdAndPassword;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

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
        if(valueOperations.get(userRequestDtoFromKfaka.getUid()) != null) {
            return false;
        }
        else {
            valueOperations.set(userRequestDtoFromKfaka.getUid(), userRequestDtoFromKfaka);
            return true;
        }
    }

    public boolean loginByIdAndPassword(IdAndPassword idAndPassword) {
        System.out.println("loginByIdAndPassword 메소드의 idAndPassword.getUid() ---> " + idAndPassword.getUid());

        Object userData = valueOperations.get(idAndPassword.getUid());
        LinkedHashMap<String, Object> userDataMap = (LinkedHashMap<String, Object>) userData;
        UserRequestDtoFromKfaka map = objectMapper.convertValue(userDataMap, UserRequestDtoFromKfaka.class);

        return idAndPassword.getUid().equals(map.getUid()) || idAndPassword.getPassword().equals(map.getPassword());
    }

    public boolean loginByIdAndPasswordTest(String username) {
        Object userData = valueOperations.get(username);

        if(userData == null) {
            return false;
        }
        return true;
    }

    public UserRequestDtoFromKfaka getUserPassword(String username) {
        Object userData = valueOperations.get(username);
        LinkedHashMap<String, Object> userDataMap = (LinkedHashMap<String, Object>) userData;
        UserRequestDtoFromKfaka map = objectMapper.convertValue(userDataMap, UserRequestDtoFromKfaka.class);

        return map;
    }
}