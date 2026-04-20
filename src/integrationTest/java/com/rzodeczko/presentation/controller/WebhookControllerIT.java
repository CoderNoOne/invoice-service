package com.rzodeczko.presentation.controller;

import com.rzodeczko.application.port.input.HandleInvoiceWebhookUseCase;
import com.rzodeczko.presentation.dto.FakturowniaWebhookDealDto;
import com.rzodeczko.presentation.dto.FakturowniaWebhookDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for WebhookController using standalone MockMvc setup.
 */
class WebhookControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void handleInvoiceUpdated_shouldReturn200AndNotCallUseCaseIfNoExternalId() throws Exception {
        HandleInvoiceWebhookUseCase handleInvoiceWebhookUseCase = mock(HandleInvoiceWebhookUseCase.class);
        WebhookController controller = new WebhookController(handleInvoiceWebhookUseCase);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        FakturowniaWebhookDealDto deal = new FakturowniaWebhookDealDto(Map.of());
        FakturowniaWebhookDto payload = new FakturowniaWebhookDto(1L, deal, "app", "token");

        mockMvc.perform(post("/webhooks/fakturownia/invoices/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verifyNoInteractions(handleInvoiceWebhookUseCase);
    }

    @Test
    void handleInvoiceUpdated_shouldReturn200AndCallUseCaseIfExternalIdPresent() throws Exception {
        HandleInvoiceWebhookUseCase handleInvoiceWebhookUseCase = mock(HandleInvoiceWebhookUseCase.class);
        WebhookController controller = new WebhookController(handleInvoiceWebhookUseCase);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        FakturowniaWebhookDealDto deal = new FakturowniaWebhookDealDto(Map.of("fakturownia", 123L));
        FakturowniaWebhookDto payload = new FakturowniaWebhookDto(1L, deal, "app", "token");

        mockMvc.perform(post("/webhooks/fakturownia/invoices/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(handleInvoiceWebhookUseCase).handle("123");
    }
}
