package com.tss.springSecurity.service;

import com.tss.springSecurity.dto.JwtAuthResponse;
import com.tss.springSecurity.dto.LoginDto;
import com.tss.springSecurity.dto.RegistrationDto;
import com.tss.springSecurity.dto.UserResponseDto;

public interface AuthService {
    UserResponseDto register(RegistrationDto registrationDto);
    JwtAuthResponse login(LoginDto loginDto);

}
