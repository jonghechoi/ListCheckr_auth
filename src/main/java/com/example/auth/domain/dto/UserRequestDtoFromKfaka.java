package com.example.auth.domain.dto;

import com.example.auth.domain.User;
import lombok.Data;

@Data
public class UserRequestDtoFromKfaka {
    private Long id;
    private String uid;
    private String password;
    private String name;

    public static User toEntity(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        User user = new User();
        user.setId(userRequestDtoFromKfaka.getId());
        user.setUid(userRequestDtoFromKfaka.getUid());
        user.setPassword(userRequestDtoFromKfaka.getPassword());
        user.setName(userRequestDtoFromKfaka.getName());

        return user;
    }
}
