package com.example.auth.domain.dto;

import com.example.auth.domain.User;
import com.example.auth.domain.UserRoleEnum;
import com.example.auth.repository.AuthRepository;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDtoFromKfaka {
    private Long id;
    private String uid;
    private String password;
    private String name;
    private UserRoleEnum role;
    private String refreshToken;


    public static UserRequestDtoFromKfaka kafkaDataToUser(MemberChanged memberChanged) {
        UserRequestDtoFromKfaka userRequestDtoFromKfaka = new UserRequestDtoFromKfaka();
        userRequestDtoFromKfaka.setId(memberChanged.getId());
        userRequestDtoFromKfaka.setUid(memberChanged.getUid());
        userRequestDtoFromKfaka.setPassword(memberChanged.getPassword());
        userRequestDtoFromKfaka.setName(memberChanged.getName());
        userRequestDtoFromKfaka.setRole(memberChanged.getRole());

        return userRequestDtoFromKfaka;
    }

//    private AuthRepository authRepository;

//    public static User toEntity(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
//        User user = new User();
//        user.setId(userRequestDtoFromKfaka.getId());
//        user.setUid(userRequestDtoFromKfaka.getUid());
//        user.setPassword(userRequestDtoFromKfaka.getPassword());
//        user.setName(userRequestDtoFromKfaka.getName());
//        user.setRole(userRequestDtoFromKfaka.getRole());
//        user.setRefreshToken(userRequestDtoFromKfaka.getRefreshToken());
//
//        return user;
//    }
}
