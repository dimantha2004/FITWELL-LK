package lk.fitwell.api.user;

import jakarta.validation.Valid;
import lk.fitwell.api.common.NotFoundException;
import lk.fitwell.api.user.UserDtos.UpdateProfileRequest;
import lk.fitwell.api.user.UserDtos.UserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping
    public UserResponse me(@AuthenticationPrincipal Long userId) {
        return UserResponse.from(load(userId));
    }

    @PutMapping
    @Transactional
    public UserResponse update(@AuthenticationPrincipal Long userId, @Valid @RequestBody UpdateProfileRequest req) {
        User user = load(userId);
        user.setFullName(req.fullName().trim());
        user.setDailyWaterGoalMl(req.dailyWaterGoalMl());
        return UserResponse.from(users.save(user));
    }

    private User load(Long userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
