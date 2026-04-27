package com.rzodeczko.presentation.controller;

import com.rzodeczko.application.port.input.GenerateInvoiceUseCase;
import com.rzodeczko.application.port.input.GetInvoicePdfUseCase;
import com.rzodeczko.application.port.input.InvoiceIssueResult;
import com.rzodeczko.presentation.dto.CreateInvoiceRequestDto;
import com.rzodeczko.presentation.dto.CreateInvoiceResponseDto;
import com.rzodeczko.presentation.mapper.CreateInvoiceResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for InvoiceController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceController Unit Tests")
class InvoiceControllerTest {

    @Mock
    private GenerateInvoiceUseCase generateInvoiceUseCase;

    @Mock
    private GetInvoicePdfUseCase getInvoicePdfUseCase;

    @Mock
    private CreateInvoiceResponseMapper createInvoiceResponseMapper;

    @InjectMocks
    private InvoiceController controller;

    @Test
    @DisplayName("Should return 201 CREATED when invoice is successfully generated")
    void createInvoice_shouldReturnCreatedResponse() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();
        BigDecimal taxRate = BigDecimal.valueOf(23);
        
        CreateInvoiceRequestDto.ItemRequestDto item = new CreateInvoiceRequestDto.ItemRequestDto(
                "Product", 1, BigDecimal.TEN, taxRate);
        CreateInvoiceRequestDto request = new CreateInvoiceRequestDto(
                orderId, "PL1234567890", "Test Buyer", List.of(item));
        
        InvoiceIssueResult result = new InvoiceIssueResult.Issued(invoiceId);
        CreateInvoiceResponseDto responseDto = new CreateInvoiceResponseDto(invoiceId, "SUCCESS", "Invoice issued");

        given(generateInvoiceUseCase.generate(any())).willReturn(result);
        given(createInvoiceResponseMapper.toResponse(result))
                .willReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseDto));

        // when
        ResponseEntity<CreateInvoiceResponseDto> response = controller.createInvoice(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().invoiceId()).isEqualTo(invoiceId);
        assertThat(response.getBody().status()).isEqualTo("SUCCESS");
        
        verify(generateInvoiceUseCase).generate(argThat(command -> 
            command.orderId().equals(orderId) && 
            command.items().get(0).taxRate().equals(taxRate)
        ));
    }

    @Test
    @DisplayName("Should return 200 OK and PDF content when requesting invoice PDF")
    void getInvoicePdf_shouldReturnPdfContent() {
        // given
        UUID id = UUID.randomUUID();
        byte[] pdf = {1, 2, 3};
        given(getInvoicePdfUseCase.getPdf(id)).willReturn(pdf);

        // when
        ResponseEntity<byte[]> response = controller.getInvoicePdf(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(pdf);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        verify(getInvoicePdfUseCase).getPdf(id);
    }
}
