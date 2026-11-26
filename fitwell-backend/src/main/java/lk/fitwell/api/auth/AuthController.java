package lk.fitwell.api.auth;

import jakarta.validation.Valid;
import lk.fitwell.api.auth.AuthDtos.AuthResponse;
import lk.fitwell.api.auth.AuthDtos.LoginRequest;
import lk.fitwell.api.auth.AuthDtos.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return auth.register(req);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req);
    }

    /** Lightweight reachability check used by the app before syncing. */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "UP", "time", Instant.now().toString());
    }
}
