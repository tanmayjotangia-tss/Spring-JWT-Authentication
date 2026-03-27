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
import jakarta.persistence.EntityManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplementationTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImplementation authService;

    @Test
    void register_successfully() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("john");
        dto.setPassword("1234");
        dto.setRole("ROLE_USER");

        Role role = new Role();
        role.setRoleName("ROLE_USER");

        role.setUsers(new ArrayList<>());

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(role));

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setUsername("john");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void register_unsuccessfull_due_to_user_exists(){
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("john");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        UserApiException exception = assertThrows(UserApiException.class, () -> authService.register(dto));

        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void registration_unsuccessfull_due_to_role_not_exist(){
        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("john");
        dto.setPassword("1234");
        dto.setRole("ROLE_USER");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(roleRepository.findByRoleName("ROLE_USER"))
                .thenReturn(Optional.empty());

        UserApiException exception = assertThrows(UserApiException.class, () -> authService.register(dto));

        assertEquals("Role does not exist", exception.getMessage());
    }

    @Test
    void login_successfull() {
        LoginDto dto = new LoginDto();
        dto.setUsername("john");
        dto.setPassword("1234");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");

        JwtAuthResponse response = authService.login(dto);
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
    }

    @Test
    void login_unsucessfull(){
        LoginDto dto = new LoginDto();
        dto.setUsername("john");
        dto.setPassword("1234");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        UserApiException exception = assertThrows(UserApiException.class, () -> authService.login(dto));
        assertEquals("Username or Password is incorrect",exception.getMessage());
    }
}