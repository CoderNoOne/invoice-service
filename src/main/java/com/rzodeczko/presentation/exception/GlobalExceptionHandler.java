package com.rzodeczko.presentation.exception;

import com.rzodeczko.application.exception.EmptyPdfResponseException;
import com.rzodeczko.application.exception.InvoiceConcurrentModificationException;
import com.rzodeczko.application.exception.TaxSystemPermanentException;
import com.rzodeczko.application.exception.TaxSystemTemporaryException;
import com.rzodeczko.domain.exception.InvoiceAlreadyExistsException;
import com.rzodeczko.domain.exception.InvoiceNotIssuedException;
import com.rzodeczko.domain.exception.ResourceNotFoundException;
import com.rzodeczko.infrastructure.webhook.access.exception.UnauthorizedWebhookAccessException;
import com.rzodeczko.infrastructure.webhook.access.exception.WebhookRateLimitExceededException;
import com.rzodeczko.presentation.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handle(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(400, "Bad Request", message));
    }

    @ExceptionHandler(UnauthorizedWebhookAccessException.class)
    public ResponseEntity<ErrorResponseDto> handle(UnauthorizedWebhookAccessException e) {
        log.warn("Unauthorized webhook access: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto(401, "Unauthorized", e.getMessage()));
    }

    @ExceptionHandler(WebhookRateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> handle(WebhookRateLimitExceededException e) {
        log.warn("Webhook rate limit exceeded: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponseDto(429, "Too Many Requests", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handle(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(400, "Bad Request", e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handle(NoResourceFoundException e) {
        log.warn("Endpoint not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(404, "Not Found", e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handle(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(404, "Not Found", e.getMessage()));
    }

    @ExceptionHandler(InvoiceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handle(InvoiceAlreadyExistsException e) {
        log.warn("Invoice already exists: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(409, "Conflict", e.getMessage()));
    }

    @ExceptionHandler(InvoiceNotIssuedException.class)
    public ResponseEntity<ErrorResponseDto> handle(InvoiceNotIssuedException e) {
        log.warn("Invoice not issued: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(409, "Conflict", e.getMessage()));
    }

    @ExceptionHandler(InvoiceConcurrentModificationException.class)
    public ResponseEntity<ErrorResponseDto> handle(InvoiceConcurrentModificationException e) {
        log.warn("Concurrent modification: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(409, "Conflict", e.getMessage() + " - please retry"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handle(DataIntegrityViolationException e) {
        log.warn("Data integrity violation: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(409, "Conflict", "Resource already exists or violates data integrity"));
    }

    @ExceptionHandler(TaxSystemPermanentException.class)
    public ResponseEntity<ErrorResponseDto> handle(TaxSystemPermanentException e) {
        log.warn("Permanent tax system error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ErrorResponseDto(
                        422,
                        "Unprocessable Content",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(TaxSystemTemporaryException.class)
    public ResponseEntity<ErrorResponseDto> handle(TaxSystemTemporaryException e) {
        log.error("Temporary tax system error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponseDto(
                        503,
                        "Service Unavailable",
                        "Temporary external tax system problem: " + e.getMessage()
                ));
    }

    @ExceptionHandler(EmptyPdfResponseException.class)
    public ResponseEntity<ErrorResponseDto> handle(EmptyPdfResponseException e) {
        log.error("Empty PDF response: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponseDto(
                        502,
                        "Bad Gateway",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handle(IllegalStateException e) {
        log.error("Illegal state: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(
                        500,
                        "Internal Server Error",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handle(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(500, "Internal Server Error", "Unexpected error"));
    }
}