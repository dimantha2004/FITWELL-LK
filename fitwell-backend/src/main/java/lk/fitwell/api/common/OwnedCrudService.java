package lk.fitwell.api.common;

import lk.fitwell.api.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Shared create/update/delete logic for all user-owned, synchronised records.
 * Feature services only describe how to copy request fields onto the entity and
 * how to turn an entity into a response (template method pattern).
 *
 * Sync rules implemented here:
 *  - create is idempotent: re-sending a POST with an id that already exists returns the stored record
 *  - update (PUT) is an upsert, so an offline create that never reached the server still succeeds
 *  - last-write-wins: an update older than the stored version is rejected with 409 + the current record
 *  - delete is idempotent: deleting an unknown id is not an error
 */
public abstract class OwnedCrudService<E extends OwnedEntity, Q extends SyncRequest, R> {

    protected final OwnedRepository<E> repository;
    private final UserRepository users;
    private final Supplier<E> factory;
    private final String label;

    protected OwnedCrudService(OwnedRepository<E> repository, UserRepository users, Supplier<E> factory, String label) {
        this.repository = repository;
        this.users = users;
        this.factory = factory;
        this.label = label;
    }

    /** Copy the request fields onto the entity. */
    protected abstract void apply(E entity, Q request, Long userId);

    protected abstract R toResponse(E entity);

    @Transactional(readOnly = true)
    public R get(Long userId, String id) {
        return toResponse(find(userId, id));
    }

    @Transactional
    public Result<R> create(Long userId, Q request) {
        String id = request.id() == null || request.id().isBlank() ? UUID.randomUUID().toString() : request.id();
        Optional<E> existing = repository.findById(id);
        if (existing.isPresent()) {
            ensureOwner(existing.get(), userId, id);
            return new Result<>(toResponse(existing.get()), false); // duplicate submission, nothing new stored
        }
        return new Result<>(toResponse(insert(userId, id, request)), true);
    }

    @Transactional
    public Result<R> update(Long userId, String id, Q request) {
        Optional<E> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return new Result<>(toResponse(insert(userId, id, request)), true);
        }
        E entity = existing.get();
        ensureOwner(entity, userId, id);
        Instant incoming = request.updatedAt() == null ? Instant.now() : request.updatedAt();
        if (incoming.isBefore(entity.getUpdatedAt())) {
            throw new ConflictException("A newer version of this " + label + " exists on the server", toResponse(entity));
        }
        apply(entity, request, userId);
        entity.setUpdatedAt(incoming);
        return new Result<>(toResponse(repository.save(entity)), false);
    }

    @Transactional
    public void delete(Long userId, String id) {
        repository.findByIdAndUserId(id, userId).ifPresent(repository::delete);
    }

    protected E find(Long userId, String id) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(label + " not found"));
    }

    private E insert(Long userId, String id, Q request) {
        E entity = factory.get();
        entity.setId(id);
        entity.setUser(users.getReferenceById(userId));
        entity.setUpdatedAt(request.updatedAt() == null ? Instant.now() : request.updatedAt());
        apply(entity, request, userId);
        return repository.save(entity);
    }

    private void ensureOwner(E entity, Long userId, String id) {
        if (!entity.getUser().getId().equals(userId)) {
            // Deliberately vague: do not reveal that the id belongs to another account.
            throw new ConflictException("Id " + id + " is already in use", null);
        }
    }

    public record Result<R>(R body, boolean created) {}
}
