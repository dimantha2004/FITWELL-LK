package lk.fitwell.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.fitwell.api.user.UserDtos.UserResponse;

import java.time.Instant;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank(message = "Name is required") @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
            String fullName,
            @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") @Size(max = 150)
            String email,
            @NotBlank(message = "Password is required")
            @Size(min = 8, max = 72, message = "Password must be 8-72 characters")
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain a letter and a number")
            String password) {}

    public record LoginRequest(
            @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") String email,
            @NotBlank(message = "Password is required") String password) {}

    public record AuthResponse(String token, Instant expiresAt, UserResponse user) {}
}
