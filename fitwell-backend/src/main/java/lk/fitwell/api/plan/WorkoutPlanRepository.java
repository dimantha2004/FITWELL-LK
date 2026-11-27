package lk.fitwell.api.plan;

import lk.fitwell.api.common.OwnedRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutPlanRepository extends OwnedRepository<WorkoutPlan> {

    @Query("""
            select p from WorkoutPlan p
            where p.user.id = :userId
              and (:difficulty is null or p.difficulty = :difficulty)
              and (:search is null or lower(p.name) like lower(concat('%', :search, '%'))
                   or lower(p.description) like lower(concat('%', :search, '%')))
            order by p.updatedAt desc""")
    List<WorkoutPlan> search(@Param("userId") Long userId,
                             @Param("search") String search,
                             @Param("difficulty") String difficulty);
}
