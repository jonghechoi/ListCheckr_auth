package com.example.auth.domain.dto;

import com.example.auth.domain.User;
import com.example.auth.domain.UserRoleEnum;
import com.example.auth.repository.AuthRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserRequestDtoFromKfaka {
    private Long id;
    private String uid;
    private String password;
    private String name;
    private UserRoleEnum role;
    private String refreshToken;

    private final AuthRepository authRepository;

    public boolean updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;

        if(authRepository.updateUser(this)) {
            return true;
        }
        return false;
    }

    public static User toEntity(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        User user = new User();
        user.setId(userRequestDtoFromKfaka.getId());
        user.setUid(userRequestDtoFromKfaka.getUid());
        user.setPassword(userRequestDtoFromKfaka.getPassword());
        user.setName(userRequestDtoFromKfaka.getName());
        user.setRole(userRequestDtoFromKfaka.getRole());
        user.setRefreshToken(userRequestDtoFromKfaka.getRefreshToken());

        return user;
    }
}
