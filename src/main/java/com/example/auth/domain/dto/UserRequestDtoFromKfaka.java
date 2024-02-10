package com.example.auth.domain.dto;

import com.example.auth.domain.User;
import com.example.auth.domain.UserRoleEnum;
import lombok.Data;

@Data
public class UserRequestDtoFromKfaka {
    private Long id;
    private String uid;
    private String password;
    private String name;
    private UserRoleEnum role;

    public static User toEntity(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        User user = new User();
        user.setId(userRequestDtoFromKfaka.getId());
        user.setUid(userRequestDtoFromKfaka.getUid());
        user.setPassword(userRequestDtoFromKfaka.getPassword());
        user.setName(userRequestDtoFromKfaka.getName());
        user.setRole(userRequestDtoFromKfaka.getRole());
//        user.setRole(UserRoleEnum.USER);

        return user;
    }
}
