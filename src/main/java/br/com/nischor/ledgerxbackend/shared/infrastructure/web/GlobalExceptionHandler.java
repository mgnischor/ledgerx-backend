package br.com.nischor.ledgerxbackend.shared.infrastructure.web;

import br.com.nischor.ledgerxbackend.identity.domain.exception.InvalidCredentialsException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.BusinessRuleViolationException;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized translation of domain and security exceptions into standardized
 * {@link ApiError} HTTP responses, applied across all {@code @RestController}s.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link EntityNotFoundException}, returning a 404 Not Found response.
     *
     * @param exception the thrown exception
     * @return a 404 response body describing the missing entity
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(HttpStatus.NOT_FOUND.value(), "Not Found", exception.getMessage()));
    }

    /**
     * Handles {@link BusinessRuleViolationException}, returning a 422 Unprocessable Entity response.
     *
     * @param exception the thrown exception
     * @return a 422 response body describing the violated business rule
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiError> handleBusinessRuleViolation(BusinessRuleViolationException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.of(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Business Rule Violation",
                        exception.getMessage()));
    }

    /**
     * Handles {@link InvalidCredentialsException}, returning a 401 Unauthorized response.
     *
     * @param exception the thrown exception
     * @return a 401 response body describing the authentication failure
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", exception.getMessage()));
    }

    /**
     * Handles Spring Security's {@link AccessDeniedException}, returning a 403 Forbidden response
     * with a generic message (the original exception message is not exposed to the client).
     *
     * @param exception the thrown exception (unused beyond triggering this handler)
     * @return a 403 response body indicating insufficient permissions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of(HttpStatus.FORBIDDEN.value(), "Forbidden",
                        "Your role does not have permission to perform this action"));
    }
}
