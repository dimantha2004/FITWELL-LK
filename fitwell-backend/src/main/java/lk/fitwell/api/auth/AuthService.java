package lk.fitwell.api.auth;

import lk.fitwell.api.auth.AuthDtos.AuthResponse;
import lk.fitwell.api.auth.AuthDtos.LoginRequest;
import lk.fitwell.api.auth.AuthDtos.RegisterRequest;
import lk.fitwell.api.common.ConflictException;
import lk.fitwell.api.security.JwtService;
import lk.fitwell.api.user.User;
import lk.fitwell.api.user.UserDtos.UserResponse;
import lk.fitwell.api.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists", null);
        }
        User user = new User();
        user.setFullName(req.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(req.password())); // BCrypt, never stored in plain text
        return respond(users.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmailIgnoreCase(req.email().trim())
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                // Same message for unknown email and wrong password, so accounts cannot be probed.
                .orElseThrow(() -> new BadCredentialsException("Incorrect email or password"));
        return respond(user);
    }

    private AuthResponse respond(User user) {
        JwtService.IssuedToken t = jwt.issue(user);
        return new AuthResponse(t.token(), t.expiresAt(), UserResponse.from(user));
    }
}
