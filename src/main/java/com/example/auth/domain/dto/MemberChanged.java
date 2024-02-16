package com.example.auth.domain.dto;

import com.example.auth.domain.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberChanged {
    private Long id;
    private String uid;
    private String password;
    private String name;
    private UserRoleEnum role;
}
