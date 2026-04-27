package com.rzodeczko.presentation.exception;

import com.rzodeczko.application.exception.EmptyPdfResponseException;
import com.rzodeczko.application.exception.InvoiceConcurrentModificationException;
import com.rzodeczko.application.exception.TaxSystemPermanentException;
import com.rzodeczko.application.exception.TaxSystemTemporaryException;
import com.rzodeczko.domain.exception.InvoiceAlreadyExistsException;
import com.rzodeczko.domain.exception.InvoiceNotIssuedException;
import com.rzodeczko.domain.exception.ResourceNotFoundException;
import com.rzodeczko.presentation.dto.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler.
 */
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND for ResourceNotFoundException")
    void handleResourceNotFoundException_shouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("not found");
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 409 CONFLICT for InvoiceAlreadyExistsException")
    void handleInvoiceAlreadyExistsException_shouldReturnConflict() {
        InvoiceAlreadyExistsException ex = new InvoiceAlreadyExistsException(java.util.UUID.randomUUID());
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should return 409 CONFLICT for InvoiceNotIssuedException")
    void handleInvoiceNotIssuedException_shouldReturnConflict() {
        InvoiceNotIssuedException ex = new InvoiceNotIssuedException(java.util.UUID.randomUUID());
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should return 409 CONFLICT for InvoiceConcurrentModificationException")
    void handleInvoiceConcurrentModificationException_shouldReturnConflict() {
        InvoiceConcurrentModificationException ex = new InvoiceConcurrentModificationException(java.util.UUID.randomUUID());
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should return 409 CONFLICT for DataIntegrityViolationException")
    void handleDataIntegrityViolationException_shouldReturnConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("violation");
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should return 422 UNPROCESSABLE CONTENT for TaxSystemPermanentException")
    void handleTaxSystemPermanentException_shouldReturnUnprocessableContent() {
        // given
        String errorMessage = "Fakturownia rejected invoice";
        TaxSystemPermanentException ex = new TaxSystemPermanentException(errorMessage);
        
        // when
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().error()).isEqualTo("Unprocessable Content");
        assertThat(response.getBody().message()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Should return 503 SERVICE UNAVAILABLE for TaxSystemTemporaryException")
    void handleTaxSystemTemporaryException_shouldReturnServiceUnavailable() {
        // given
        String errorMessage = "Fakturownia communication error";
        TaxSystemTemporaryException ex = new TaxSystemTemporaryException(errorMessage);
        
        // when
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(503);
        assertThat(response.getBody().error()).isEqualTo("Service Unavailable");
        assertThat(response.getBody().message()).contains(errorMessage);
    }

    @Test
    @DisplayName("Should return 502 BAD GATEWAY for EmptyPdfResponseException")
    void handleEmptyPdfResponseException_shouldReturnBadGateway() {
        EmptyPdfResponseException ex = new EmptyPdfResponseException("id");
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(502);
    }

    @Test
    @DisplayName("Should return 500 INTERNAL SERVER ERROR for generic Exception")
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("error");
        ResponseEntity<ErrorResponseDto> response = handler.handle(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
    }
}
