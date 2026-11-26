package lk.fitwell.api.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UserDtos {
    private UserDtos() {}

    public record UserResponse(Long id, String fullName, String email, int dailyWaterGoalMl) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getDailyWaterGoalMl());
        }
    }

    public record UpdateProfileRequest(
            @NotBlank(message = "Name is required") @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
            String fullName,
            @Min(value = 500, message = "Daily goal must be at least 500 ml")
            @Max(value = 8000, message = "Daily goal cannot exceed 8000 ml")
            int dailyWaterGoalMl) {}
}
