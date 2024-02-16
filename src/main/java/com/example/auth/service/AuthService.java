package com.example.auth.service;

import com.example.auth.domain.dto.UserRequestDtoFromKfaka;

public interface AuthService {
    boolean save(UserRequestDtoFromKfaka userRequestDtoFromKfaka);
}
