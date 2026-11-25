package lk.fitwell.api.common;

import java.time.Instant;
import java.util.Map;

/** Uniform JSON error body. "current" carries the server copy of a record on a 409 conflict. */
public record ApiError(Instant timestamp, int status, String error, String message,
                       Map<String, String> fieldErrors, Object current) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, null, null);
    }
}
