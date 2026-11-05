package com.blog.service;

import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.response.AuthResponse;
import com.blog.dto.response.UserSummaryResponse;
import com.blog.entity.Role;
import com.blog.entity.User;
import com.blog.exception.BadRequestException;
import com.blog.exception.EmailAlreadyExistsException;
import com.blog.mapper.UserMapper;
import com.blog.repository.UserRepository;
import com.blog.security.JwtTokenProvider;
import com.blog.security.UserPrincipal;
import com.blog.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private UserSummaryResponse sampleUserSummary;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("sushil")
                .email("sushil@example.com")
                .password("encoded_password")
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        sampleUserSummary = UserSummaryResponse.builder()
                .id(1L)
                .username("sushil")
                .email("sushil@example.com")
                .role(Role.ROLE_USER)
                .createdAt(sampleUser.getCreatedAt())
                .build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest request = RegisterRequest.builder()
                .username("sushil")
                .email("sushil@example.com")
                .password("Password@123")
                .build();

        when(userRepository.existsByEmail("sushil@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("sushil")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateTokenFromUser(1L, "sushil@example.com", "ROLE_USER")).thenReturn("mock-jwt-token");
        when(userMapper.toUserSummaryResponse(sampleUser)).thenReturn(sampleUserSummary);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("sushil@example.com", response.getUser().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException on duplicate email registration")
    void shouldThrowExceptionWhenEmailExists() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("sushil@example.com")
                .password("Password@123")
                .build();

        when(userRepository.existsByEmail("sushil@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException on duplicate username registration")
    void shouldThrowExceptionWhenUsernameExists() {
        RegisterRequest request = RegisterRequest.builder()
                .username("sushil")
                .email("another@example.com")
                .password("Password@123")
                .build();

        when(userRepository.existsByEmail("another@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("sushil")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        LoginRequest request = LoginRequest.builder()
                .email("sushil@example.com")
                .password("Password@123")
                .build();

        UserPrincipal principal = UserPrincipal.create(sampleUser);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mock-login-jwt");
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toUserSummaryResponse(sampleUser)).thenReturn(sampleUserSummary);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-login-jwt", response.getToken());
        assertEquals("sushil@example.com", response.getUser().getEmail());
        assertEquals("User logged in successfully", response.getMessage());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid login password")
    void shouldThrowExceptionOnFailedLogin() {
        LoginRequest request = LoginRequest.builder()
                .email("sushil@example.com")
                .password("WrongPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}

