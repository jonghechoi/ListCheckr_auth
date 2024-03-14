package com.example.auth.controller;

import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.service.impl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class AuthRestController {
    private final AuthServiceImpl authService;

    public AuthRestController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("Login success", HttpStatus.OK);
    }

    @GetMapping("/login/jwt-test")
    public ResponseEntity<String> jwtTest() {
        return new ResponseEntity<>("jwt-test success", HttpStatus.OK);
    }

    @GetMapping("/login/jwt-test-refreshtoken")
    public ResponseEntity<String> jwtRefreshTokenTest() {
        return new ResponseEntity<>("jwt-test-refreshtoken success", HttpStatus.OK);
    }

    /**
     * Kafka로부터 전달된 인증에 필요한 정보 데이터 저장 (테스트용)
     */
    @PostMapping("/login/save-test")
    public ResponseEntity<String> saveTest(@RequestBody UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        if(authService.save(userRequestDtoFromKfaka)) {
            return new ResponseEntity<>("ok", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("no", HttpStatus.FORBIDDEN);
        }
    }
}