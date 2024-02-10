package com.example.auth.controller;

import com.example.auth.domain.dto.IdAndPassword;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.service.impl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/user")
public class AuthRestController {
    private final AuthServiceImpl authService;

    public AuthRestController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> read(@RequestBody IdAndPassword idAndPassword) {
        boolean check = Optional.of(authService.getLoginByIdAndPassword(idAndPassword)).isPresent();
        if(check) {
            return new ResponseEntity<>("ok", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("no", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login/saveTest")
    public ResponseEntity<String> saveTest(@RequestBody UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        if(authService.save(userRequestDtoFromKfaka)) {
            return new ResponseEntity<>("ok", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("no", HttpStatus.FORBIDDEN);
        }
    }
}