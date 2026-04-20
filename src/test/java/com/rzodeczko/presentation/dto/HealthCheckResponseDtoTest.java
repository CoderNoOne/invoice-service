package com.rzodeczko.presentation.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HealthCheckResponseDto.
 */
class HealthCheckResponseDtoTest {

    @Test
    void constructor_shouldCreateDto() {
        String message = "OK";
        HealthCheckResponseDto dto = new HealthCheckResponseDto(message);
        assertEquals(message, dto.message());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        HealthCheckResponseDto dto1 = new HealthCheckResponseDto("OK");
        HealthCheckResponseDto dto2 = new HealthCheckResponseDto("OK");
        assertEquals(dto1, dto2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentValues() {
        HealthCheckResponseDto dto1 = new HealthCheckResponseDto("OK");
        HealthCheckResponseDto dto2 = new HealthCheckResponseDto("ERROR");
        assertNotEquals(dto1, dto2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        HealthCheckResponseDto dto1 = new HealthCheckResponseDto("OK");
        HealthCheckResponseDto dto2 = new HealthCheckResponseDto("OK");
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_shouldContainMessage() {
        HealthCheckResponseDto dto = new HealthCheckResponseDto("OK");
        assertTrue(dto.toString().contains("OK"));
    }
}
