package lk.fitwell.api.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface OwnedRepository<E extends OwnedEntity> extends JpaRepository<E, String> {
    Optional<E> findByIdAndUserId(String id, Long userId);
}
