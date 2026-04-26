package com.rzodeczko.presentation.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FakturowniaWebhookDto.
 */
class FakturowniaWebhookDtoTest {

    @Test
    void constructor_shouldCreateDto() {
        Long id = 1L;
        FakturowniaWebhookDealDto deal = new FakturowniaWebhookDealDto(Map.of("key", 1L));
        String appName = "app";
        String apiToken = "sharedSecret";
        FakturowniaWebhookDto dto = new FakturowniaWebhookDto(id, deal, appName, apiToken);
        assertEquals(id, dto.id());
        assertEquals(deal, dto.deal());
        assertEquals(appName, dto.appName());
        assertEquals(apiToken, dto.apiToken());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        FakturowniaWebhookDealDto deal = new FakturowniaWebhookDealDto(Map.of("key", 1L));
        FakturowniaWebhookDto dto1 = new FakturowniaWebhookDto(1L, deal, "app", "sharedSecret");
        FakturowniaWebhookDto dto2 = new FakturowniaWebhookDto(1L, deal, "app", "sharedSecret");
        assertEquals(dto1, dto2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        FakturowniaWebhookDealDto deal = new FakturowniaWebhookDealDto(Map.of("key", 1L));
        FakturowniaWebhookDto dto1 = new FakturowniaWebhookDto(1L, deal, "app", "sharedSecret");
        FakturowniaWebhookDto dto2 = new FakturowniaWebhookDto(1L, deal, "app", "sharedSecret");
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
