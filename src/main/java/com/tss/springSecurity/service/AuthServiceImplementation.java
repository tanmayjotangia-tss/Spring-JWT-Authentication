package com.tss.springSecurity.service;

import com.tss.springSecurity.dto.JwtAuthResponse;
import com.tss.springSecurity.dto.LoginDto;
import com.tss.springSecurity.dto.RegistrationDto;
import com.tss.springSecurity.dto.UserResponseDto;
import com.tss.springSecurity.entity.Role;
import com.tss.springSecurity.entity.User;
import com.tss.springSecurity.exception.UserApiException;
import com.tss.springSecurity.repository.RoleRepository;
import com.tss.springSecurity.repository.UserRepository;
import com.tss.springSecurity.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public UserResponseDto register(RegistrationDto registrationDto) {
        if(userRepository.existsByUsername(registrationDto.getUsername()))
            throw new UserApiException(HttpStatus.BAD_REQUEST,"User already exists");
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        Role userRole = roleRepository.findByRoleName(registrationDto.getRole())
                .orElseThrow(()-> new UserApiException(HttpStatus.BAD_REQUEST, "Role does not exist"));

        userRole.getUsers().add(user);
        user.setRole(userRole);

        user = userRepository.save(user);

        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        return dto;
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword())
            );

//            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
            jwtAuthResponse.setAccessToken(token);
            return jwtAuthResponse;
        } catch (BadCredentialsException exception) {
            throw new UserApiException(HttpStatus.NOT_FOUND,"Username or Password is incorrect");
        }
    }
}
