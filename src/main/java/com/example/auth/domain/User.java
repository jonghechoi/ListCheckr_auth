package com.example.auth.domain;

import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
//@Entity
@RedisHash
@AllArgsConstructor
public class User {
    @Id
    private Long id;
    private String uid;
    private String password;
    private String name;

    public User() {}

    @Override
    public String toString() {
        return "id : " + getId() + " name : " + getName();
    }
}
