package com.example.auth.login.service.impl;

import com.example.auth.domain.User;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!authRepository.isUidExist(username)) {
            throw new RuntimeException("일치하는 유저가 없습니다.");
        }

        UserRequestDtoFromKfaka userRequestDtoFromKfaka = authRepository.getUser(username);
        User user = new User(userRequestDtoFromKfaka.getUid(),
                            userRequestDtoFromKfaka.getPassword(),
                            userRequestDtoFromKfaka.getName(),
                            userRequestDtoFromKfaka.getRole(),
                            userRequestDtoFromKfaka.getRefreshToken());

        return new UserDetailsImpl(user, user.getUid(), user.getPassword());
    }
}
