package com.rzodeczko.presentation.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FakturowniaWebhookDealDto.
 */
class FakturowniaWebhookDealDtoTest {

    @Test
    void constructor_shouldCreateDto() {
        Map<String, Long> externalIds = Map.of("key", 1L);
        FakturowniaWebhookDealDto dto = new FakturowniaWebhookDealDto(externalIds);
        assertEquals(externalIds, dto.externalIds());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        Map<String, Long> externalIds = Map.of("key", 1L);
        FakturowniaWebhookDealDto dto1 = new FakturowniaWebhookDealDto(externalIds);
        FakturowniaWebhookDealDto dto2 = new FakturowniaWebhookDealDto(externalIds);
        assertEquals(dto1, dto2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        Map<String, Long> externalIds = Map.of("key", 1L);
        FakturowniaWebhookDealDto dto1 = new FakturowniaWebhookDealDto(externalIds);
        FakturowniaWebhookDealDto dto2 = new FakturowniaWebhookDealDto(externalIds);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
