package com.example.auth.service;

import com.example.auth.domain.User;
import com.example.auth.domain.dto.IdAndPassword;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;

import java.util.Optional;

public interface AuthService {
    boolean save(UserRequestDtoFromKfaka userRequestDtoFromKfaka);

    Optional<User> getLoginByIdAndPassword(IdAndPassword idAndPassword);
}
