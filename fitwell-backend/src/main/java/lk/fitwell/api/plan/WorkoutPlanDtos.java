package lk.fitwell.api.plan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lk.fitwell.api.common.SyncRequest;

import java.time.Instant;
import java.util.List;

public final class WorkoutPlanDtos {
    private WorkoutPlanDtos() {}

    public static final String CATEGORIES = "STRENGTH|CARDIO|HIIT|YOGA|RUNNING|WALKING|CYCLING|SPORTS|OTHER";
    public static final String DIFFICULTIES = "BEGINNER|INTERMEDIATE|ADVANCED";

    public record ExerciseDto(
            @NotBlank(message = "Exercise name is required") @Size(max = 100) String name,
            @Min(value = 1, message = "Sets must be at least 1") @Max(value = 20, message = "Sets cannot exceed 20") int sets,
            @Min(value = 1, message = "Reps must be at least 1") @Max(value = 200, message = "Reps cannot exceed 200") int reps,
            @Min(value = 0, message = "Rest cannot be negative") @Max(value = 600, message = "Rest cannot exceed 600 s") int restSeconds) {}

    public record PlanRequest(
            @Size(max = 36) String id,
            @NotBlank(message = "Plan name is required") @Size(max = 100, message = "Name is too long") String name,
            @Size(max = 500, message = "Description is too long") String description,
            @NotBlank @Pattern(regexp = CATEGORIES, message = "Unknown category") String category,
            @NotBlank @Pattern(regexp = DIFFICULTIES, message = "Difficulty must be BEGINNER, INTERMEDIATE or ADVANCED") String difficulty,
            @Min(value = 5, message = "Duration must be at least 5 minutes") @Max(value = 300, message = "Duration cannot exceed 300 minutes") int durationMinutes,
            @NotNull @Size(max = 30, message = "A plan can have at most 30 exercises") List<@Valid ExerciseDto> exercises,
            Instant updatedAt) implements SyncRequest {}

    public record PlanResponse(String id, String name, String description, String category, String difficulty,
                               int durationMinutes, List<ExerciseDto> exercises, Instant createdAt, Instant updatedAt) {}
}
