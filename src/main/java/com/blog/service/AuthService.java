package com.blog.service;

import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.response.AuthResponse;
import com.blog.dto.response.UserSummaryResponse;
import com.blog.security.UserPrincipal;

/**
 * Service interface for authentication, user registration, and profile resolution.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

    UserSummaryResponse getCurrentUserProfile(UserPrincipal currentUser);
}

