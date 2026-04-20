package com.rzodeczko.presentation.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ErrorResponseDto.
 */
class ErrorResponseDtoTest {

    @Test
    void constructor_shouldCreateDto() {
        int status = 400;
        String error = "Bad Request";
        String message = "Invalid input";
        Instant timestamp = Instant.now();
        ErrorResponseDto dto = new ErrorResponseDto(status, error, message, timestamp);
        assertEquals(status, dto.status());
        assertEquals(error, dto.error());
        assertEquals(message, dto.message());
        assertEquals(timestamp, dto.timestamp());
    }

    @Test
    void constructor_shouldCreateDtoWithCurrentTimestamp() {
        int status = 400;
        String error = "Bad Request";
        String message = "Invalid input";
        ErrorResponseDto dto = new ErrorResponseDto(status, error, message);
        assertEquals(status, dto.status());
        assertEquals(error, dto.error());
        assertEquals(message, dto.message());
        assertNotNull(dto.timestamp());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        Instant timestamp = Instant.now();
        ErrorResponseDto dto1 = new ErrorResponseDto(400, "Error", "Msg", timestamp);
        ErrorResponseDto dto2 = new ErrorResponseDto(400, "Error", "Msg", timestamp);
        assertEquals(dto1, dto2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        Instant timestamp = Instant.now();
        ErrorResponseDto dto1 = new ErrorResponseDto(400, "Error", "Msg", timestamp);
        ErrorResponseDto dto2 = new ErrorResponseDto(400, "Error", "Msg", timestamp);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
