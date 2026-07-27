package br.com.nischor.ledgerxbackend.shared.infrastructure.web;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response body returned by the API for handled exceptions, produced by
 * {@link GlobalExceptionHandler}.
 *
 * @param timestamp the instant at which the error was produced
 * @param status    the HTTP status code
 * @param error     a short label describing the HTTP status (e.g. "Not Found")
 * @param message   a human-readable description of the error
 * @param details   optional additional details (e.g. per-field validation errors); empty when unused
 */
public record ApiError(Instant timestamp, int status, String error, String message, List<String> details) {

    /**
     * Creates an {@code ApiError} with the current instant as timestamp and no additional details.
     *
     * @param status  the HTTP status code
     * @param error   a short label describing the HTTP status
     * @param message a human-readable description of the error
     * @return a new {@code ApiError} instance
     */
    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, List.of());
    }
}
