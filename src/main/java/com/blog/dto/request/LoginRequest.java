package com.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for user authentication login requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload required to authenticate an existing user")
public class LoginRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    @Schema(description = "Registered email address", example = "sushil@example.com")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Schema(description = "Account password", example = "Password@123")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
