package com.example.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    public User(String uid, String password, String name, UserRoleEnum role) {
        this.uid = uid;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    @Override
    public String toString() {
        return "id : " + getId() + " name : " + getName();
    }
}
