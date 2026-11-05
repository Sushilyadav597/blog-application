package com.blog.dto.response;

import com.blog.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Safe public representation of a user without sensitive fields (e.g., password).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Safe summary of user profile information")
public class UserSummaryResponse {

    @Schema(description = "Unique user identifier", example = "1")
    private Long id;

    @Schema(description = "User display username", example = "sushil")
    private String username;

    @Schema(description = "User registered email", example = "sushil@example.com")
    private String email;

    @Schema(description = "Assigned security role", example = "ROLE_USER")
    private Role role;

    @Schema(description = "Account registration timestamp", example = "2026-11-05T12:00:00")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
