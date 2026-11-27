package lk.fitwell.api.plan;

import lk.fitwell.api.common.OwnedCrudService;
import lk.fitwell.api.plan.WorkoutPlanDtos.ExerciseDto;
import lk.fitwell.api.plan.WorkoutPlanDtos.PlanRequest;
import lk.fitwell.api.plan.WorkoutPlanDtos.PlanResponse;
import lk.fitwell.api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkoutPlanService extends OwnedCrudService<WorkoutPlan, PlanRequest, PlanResponse> {

    private final WorkoutPlanRepository plans;

    public WorkoutPlanService(WorkoutPlanRepository plans, UserRepository users) {
        super(plans, users, WorkoutPlan::new, "workout plan");
        this.plans = plans;
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> list(Long userId, String search, String difficulty) {
        String s = search == null || search.isBlank() ? null : search.trim();
        String d = difficulty == null || difficulty.isBlank() ? null : difficulty.toUpperCase();
        return plans.search(userId, s, d).stream().map(this::toResponse).toList();
    }

    @Override
    protected void apply(WorkoutPlan p, PlanRequest r, Long userId) {
        p.setName(r.name().trim());
        p.setDescription(r.description());
        p.setCategory(r.category());
        p.setDifficulty(r.difficulty());
        p.setDurationMinutes(r.durationMinutes());
        p.getExercises().clear();
        r.exercises().forEach(e -> p.getExercises().add(new Exercise(e.name().trim(), e.sets(), e.reps(), e.restSeconds())));
    }

    @Override
    protected PlanResponse toResponse(WorkoutPlan p) {
        List<ExerciseDto> exercises = p.getExercises().stream()
                .map(e -> new ExerciseDto(e.getName(), e.getSets(), e.getReps(), e.getRestSeconds()))
                .toList();
        return new PlanResponse(p.getId(), p.getName(), p.getDescription(), p.getCategory(), p.getDifficulty(),
                p.getDurationMinutes(), exercises, p.getCreatedAt(), p.getUpdatedAt());
    }
}
