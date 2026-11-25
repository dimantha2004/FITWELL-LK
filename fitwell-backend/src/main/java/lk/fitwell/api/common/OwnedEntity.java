package lk.fitwell.api.common;

import jakarta.persistence.*;
import lk.fitwell.api.user.User;

import java.time.Instant;

/**
 * Base class for every record that belongs to a user and is synchronised from the phone.
 * The id is a UUID generated on the device, so a record created offline keeps the same
 * identity on the server. updatedAt is the device's last-modified time and is used for
 * last-write-wins conflict detection.
 */
@MappedSuperclass
public abstract class OwnedEntity {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
