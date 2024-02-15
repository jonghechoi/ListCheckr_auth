package com.example.auth.service.impl;

import com.example.auth.domain.User;
import com.example.auth.domain.UserRoleEnum;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthServiceImplTest {
    @Autowired
    private RedisTemplate<String, UserRequestDtoFromKfaka> redisTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("id, pw Login Check")
    void getLoginByIdAndPassword() {
        // given
        IdAndPassword idAndPassword = new IdAndPassword();
        idAndPassword.setUid("sosinnmi");
        idAndPassword.setPassword("1234");

        User userExpect = new User("sosinnmi", "1234", "choi", UserRoleEnum.USER, "수정필요");

        // when
        ValueOperations<String, UserRequestDtoFromKfaka> valueOperations = redisTemplate.opsForValue();
        Object rawValue = valueOperations.get(idAndPassword.getUid());

        String uid = "";
        if (rawValue instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> valueMap = (LinkedHashMap<String, Object>) rawValue;
            UserRequestDtoFromKfaka map = objectMapper.convertValue(valueMap, UserRequestDtoFromKfaka.class);
            uid = map.getUid();
        }

        // then
        assertEquals(userExpect.getUid(), uid);
    }
}