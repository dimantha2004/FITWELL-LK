package lk.fitwell.api.plan;

import jakarta.persistence.*;
import lk.fitwell.api.common.OwnedEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlan extends OwnedEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @ElementCollection
    @CollectionTable(name = "plan_exercises", joinColumns = @JoinColumn(name = "plan_id"))
    @OrderColumn(name = "position")
    private List<Exercise> exercises = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public List<Exercise> getExercises() { return exercises; }
}
