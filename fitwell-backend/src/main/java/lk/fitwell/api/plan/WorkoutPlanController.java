package lk.fitwell.api.plan;

import jakarta.validation.Valid;
import lk.fitwell.api.common.Responses;
import lk.fitwell.api.plan.WorkoutPlanDtos.PlanRequest;
import lk.fitwell.api.plan.WorkoutPlanDtos.PlanResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class WorkoutPlanController {

    private final WorkoutPlanService service;

    public WorkoutPlanController(WorkoutPlanService service) {
        this.service = service;
    }

    @GetMapping
    public List<PlanResponse> list(@AuthenticationPrincipal Long userId,
                                   @RequestParam(required = false) String search,
                                   @RequestParam(required = false) String difficulty) {
        return service.list(userId, search, difficulty);
    }

    @GetMapping("/{id}")
    public PlanResponse get(@AuthenticationPrincipal Long userId, @PathVariable String id) {
        return service.get(userId, id);
    }

    @PostMapping
    public ResponseEntity<PlanResponse> create(@AuthenticationPrincipal Long userId, @Valid @RequestBody PlanRequest req) {
        return Responses.of(service.create(userId, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> update(@AuthenticationPrincipal Long userId, @PathVariable String id,
                                               @Valid @RequestBody PlanRequest req) {
        return Responses.of(service.update(userId, id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId, @PathVariable String id) {
        service.delete(userId, id);
    }
}
