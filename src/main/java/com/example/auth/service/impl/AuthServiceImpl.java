package com.example.auth.service.impl;

import com.example.auth.domain.User;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.repository.AuthRepository;
import com.example.auth.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private AuthRepository authRepository;

    AuthServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public boolean save(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        return authRepository.save(userRequestDtoFromKfaka);
    }
}