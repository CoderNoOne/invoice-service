package com.rzodeczko.infrastructure.fakturownia.adapter;


import com.rzodeczko.application.exception.ExternalTaxSystemException;
import com.rzodeczko.application.port.output.TaxSystemPort;
import com.rzodeczko.domain.model.Invoice;
import com.rzodeczko.infrastructure.configuration.properties.FakturowniaProperties;
import com.rzodeczko.infrastructure.fakturownia.dto.FakturowniaCreateInvoiceResponseDto;
import com.rzodeczko.infrastructure.fakturownia.dto.CreateInvoiceDto;
import com.rzodeczko.infrastructure.fakturownia.dto.CreateInvoiceWrapperDto;
import com.rzodeczko.infrastructure.fakturownia.dto.PositionDto;
import com.rzodeczko.presentation.dto.FakturowniaGetInvoiceDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Adapter for integrating with the Fakturownia external tax system API.
 * <p>
 * This class implements the {@link com.rzodeczko.application.port.output.TaxSystemPort} interface
 * and provides methods to issue invoices and retrieve invoice PDFs using the Fakturownia API.
 * It handles communication, error mapping, and request/response transformation between the domain model
 * and the external system.
 * <p>
 * Main responsibilities:
 * <ul>
 *     <li>Issue invoices by mapping domain {@link com.rzodeczko.domain.model.Invoice} to Fakturownia API requests.</li>
 *     <li>Retrieve invoice PDFs by external invoice ID.</li>
 *     <li>Translate Fakturownia API errors to domain-specific exceptions.</li>
 * </ul>
 *
 *
 */
@Component
public class FakturowniaAdapter implements TaxSystemPort {
    private final RestClient restClient;
    private final FakturowniaProperties fakturowniaProperties;

    public FakturowniaAdapter(
            RestClient.Builder restClientBuilder,
            FakturowniaProperties fakturowniaProperties
    ) {
        this.fakturowniaProperties = fakturowniaProperties;
        this.restClient = restClientBuilder
                .baseUrl(fakturowniaProperties.url())
                .build();
    }

    @Override
    public String issueInvoice(Invoice invoice) {
        try {
            var response = restClient.post()
                    .uri(uri -> uri
                            .path("/invoices.json")
                            .queryParam("api_token", fakturowniaProperties.token())
                            .build())
                    .body(mapToRequest(invoice))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new IllegalArgumentException(
                                "Fakturownia rejected invoice. status=" + res.getStatusCode()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new ExternalTaxSystemException(
                                "Fakturownia unavailable. status=" + res.getStatusCode()
                        );
                    })
                    .body(FakturowniaCreateInvoiceResponseDto.class);

            if (response == null || response.id() == null) {
                throw new ExternalTaxSystemException(
                        "Fakturownia returned no ID for invoice. orderId=" + invoice.getOrderId());
            }

            return String.valueOf(response.id());
        } catch (ExternalTaxSystemException | IllegalArgumentException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ExternalTaxSystemException(
                    "Communication error with Fakturownia. orderId=" + invoice.getOrderId(), e);
        }
    }

    @Override
    public byte[] getPdf(String externalId) {
        try {
            return restClient.get()
                    .uri(uri -> uri
                            .path("/invoices/{id}.pdf")
                            .queryParam("api_token", fakturowniaProperties.token())
                            .build(externalId))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new IllegalArgumentException(
                                "Fakturownia: PDF not found for id=" + externalId
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new ExternalTaxSystemException(
                                "Fakturownia unavailable when fetching PDF. status=" + res.getStatusCode());
                    })
                    .body(byte[].class);
        } catch (ExternalTaxSystemException | IllegalArgumentException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ExternalTaxSystemException(
                    "Communication error with Fakturownia when fetching PDF. externalId=" + externalId,
                    e
            );
        }
    }

    @Override
    public List<FakturowniaGetInvoiceDto> findByOrderId(String orderId) {
        try {
            return restClient.get()
                    .uri(uri -> uri
                            .path("/invoices")
                            .queryParam("api_token", fakturowniaProperties.token())
                            .queryParam("oid", orderId)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new IllegalArgumentException(
                                "Fakturownia rejected request when fetching by orderId=" + orderId + ". status=" + res.getStatusCode()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new ExternalTaxSystemException(
                                "Fakturownia unavailable when fetching invoices=" + res.getStatusCode());
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (ExternalTaxSystemException | IllegalArgumentException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ExternalTaxSystemException(
                    "Communication error with Fakturownia when fetching invoices. orderId=" + orderId, e);
        }
    }

    private CreateInvoiceWrapperDto mapToRequest(Invoice invoice) {
        LocalDate now = LocalDate.now();
        List<PositionDto> positions = invoice
                .getItems()
                .stream()
                .map(item -> new PositionDto(
                        item.name(),
                        23,
                        item.quantity(),
                        item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()))
                ))
                .toList();

        return new CreateInvoiceWrapperDto(new CreateInvoiceDto(
                "vat",
                now.toString(),
                now.toString(),
                now.plusDays(7).toString(),
                invoice.getBuyerName(),
                invoice.getTaxId(),
                invoice.getOrderId().toString(),
                positions
        ));
    }
}
