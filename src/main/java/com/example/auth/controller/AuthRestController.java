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

//    @PostMapping("/login")
//    public ResponseEntity<String> read(@RequestBody IdAndPassword idAndPassword) {
//        System.out.println(idAndPassword.getUid());
//        System.out.println(idAndPassword.getPassword());
//        System.out.println("idAndPassword --> " + idAndPassword);
//        if(authService.loginByIdAndPassword(idAndPassword)) {
//            return new ResponseEntity<>("ok", HttpStatus.OK);
//        }
//        else {
//            return new ResponseEntity<>("no", HttpStatus.NOT_FOUND);
//        }
//    }
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    /**
     * Kafka로부터 전달된 인증에 필요한 정보 데이터 저장
     * @param userRequestDtoFromKfaka
     * @return
     */
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