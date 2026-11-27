package lk.fitwell.api.plan;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** One exercise line inside a workout plan, stored in the plan_exercises table. */
@Embeddable
public class Exercise {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int sets;

    @Column(nullable = false)
    private int reps;

    @Column(name = "rest_seconds", nullable = false)
    private int restSeconds;

    protected Exercise() {}

    public Exercise(String name, int sets, int reps, int restSeconds) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.restSeconds = restSeconds;
    }

    public String getName() { return name; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public int getRestSeconds() { return restSeconds; }
}
