package com.example.auth.service.impl;

import com.example.auth.domain.dto.IdAndPassword;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {
    private AuthRepository authRepository;

    AuthServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public boolean save(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        return authRepository.save(userRequestDtoFromKfaka);
    }

    public boolean loginByIdAndPassword(IdAndPassword idAndPassword) {
        return authRepository.loginByIdAndPassword(idAndPassword);
    }
}