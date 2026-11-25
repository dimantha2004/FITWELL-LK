package lk.fitwell.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class Responses {
    private Responses() {}

    /** 201 Created when a row was inserted, 200 OK when an existing one was returned or updated. */
    public static <T> ResponseEntity<T> of(OwnedCrudService.Result<T> r) {
        return ResponseEntity.status(r.created() ? HttpStatus.CREATED : HttpStatus.OK).body(r.body());
    }
}
