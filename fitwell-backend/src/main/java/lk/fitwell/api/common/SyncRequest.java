package lk.fitwell.api.common;

import java.time.Instant;

/** Fields every create/update request carries so the server can apply the sync rules. */
public interface SyncRequest {
    /** Client generated UUID. Optional on POST (the server generates one if it is missing). */
    String id();

    /** When the record was last modified on the device. Used for last-write-wins. */
    Instant updatedAt();
}
