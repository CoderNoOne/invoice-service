package com.rzodeczko.presentation.controller;

import com.rzodeczko.application.port.input.GenerateInvoiceUseCase;
import com.rzodeczko.application.port.input.GetInvoicePdfUseCase;
import com.rzodeczko.presentation.dto.CreateInvoiceRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for InvoiceController using standalone MockMvc setup.
 */
class InvoiceControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createInvoice_shouldReturn201WithInvoiceId() throws Exception {
        GenerateInvoiceUseCase generateInvoiceUseCase = mock(GenerateInvoiceUseCase.class);
        GetInvoicePdfUseCase getInvoicePdfUseCase = mock(GetInvoicePdfUseCase.class);
        InvoiceController controller = new InvoiceController(generateInvoiceUseCase, getInvoicePdfUseCase);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        UUID orderId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();
        CreateInvoiceRequestDto request = new CreateInvoiceRequestDto(
                orderId,
                "1234567890",
                "Buyer Name",
                List.of(new CreateInvoiceRequestDto.ItemRequestDto("Item", 1, BigDecimal.TEN))
        );

        when(generateInvoiceUseCase.generate(any())).thenReturn(invoiceId);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceId").value(invoiceId.toString()));
    }

    @Test
    void getInvoicePdf_shouldReturnPdf() throws Exception {
        GenerateInvoiceUseCase generateInvoiceUseCase = mock(GenerateInvoiceUseCase.class);
        GetInvoicePdfUseCase getInvoicePdfUseCase = mock(GetInvoicePdfUseCase.class);
        InvoiceController controller = new InvoiceController(generateInvoiceUseCase, getInvoicePdfUseCase);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        UUID invoiceId = UUID.randomUUID();
        byte[] pdfContent = new byte[]{1, 2, 3};

        when(getInvoicePdfUseCase.getPdf(invoiceId)).thenReturn(pdfContent);

        mockMvc.perform(get("/invoices/{id}/pdf", invoiceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes(pdfContent));
    }
}
