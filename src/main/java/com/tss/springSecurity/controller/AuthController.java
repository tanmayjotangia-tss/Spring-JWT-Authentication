package com.tss.springSecurity.controller;

import com.tss.springSecurity.dto.JwtAuthResponse;
import com.tss.springSecurity.dto.LoginDto;
import com.tss.springSecurity.dto.RegistrationDto;
import com.tss.springSecurity.dto.UserResponseDto;
import com.tss.springSecurity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegistrationDto registrationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(registrationDto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.login(loginDto));
    }
}