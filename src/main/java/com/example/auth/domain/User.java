package com.example.auth.domain;

import com.example.auth.global.jwt.service.impl.JwtServiceImpl;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String uid;
    private String password;
    private String name;
    private UserRoleEnum role;
    private String refreshToken;

    public User(String uid, String password, String name, UserRoleEnum role) {
        this.uid = uid;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public User(String uid, String password, String name, UserRoleEnum role, String refreshToken) {
        this.uid = uid;
        this.password = password;
        this.name = name;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(String refreshToken) {

    }

    @Override
    public String toString() {
        return "id : " + getId() + " name : " + getName();
    }
}
