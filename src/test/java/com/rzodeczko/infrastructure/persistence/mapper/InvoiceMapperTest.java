package com.rzodeczko.infrastructure.persistence.mapper;

import com.rzodeczko.domain.model.Invoice;
import com.rzodeczko.domain.model.InvoiceItem;
import com.rzodeczko.domain.model.InvoiceStatus;
import com.rzodeczko.domain.vo.TaxRate;
import com.rzodeczko.infrastructure.persistence.entity.InvoiceEntity;
import com.rzodeczko.infrastructure.persistence.entity.InvoiceItemEmbeddable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvoiceMapper.
 */
@DisplayName("InvoiceMapper Unit Tests")
class InvoiceMapperTest {
    private final InvoiceMapper mapper = new InvoiceMapper();

    @Test
    @DisplayName("Should map domain Invoice to InvoiceEntity with all fields")
    void toEntity_shouldMapDomainToEntity() {
        // given
        Invoice invoice = new Invoice(UUID.randomUUID(), UUID.randomUUID(), "1234567890", "Buyer",
                List.of(new InvoiceItem("item", 2, BigDecimal.TEN, TaxRate.of(23))));
        
        // when
        InvoiceEntity entity = mapper.toEntity(invoice);
        
        // then
        assertThat(entity.getId()).isEqualTo(invoice.getId());
        assertThat(entity.getOrderId()).isEqualTo(invoice.getOrderId());
        assertThat(entity.getBuyerName()).isEqualTo(invoice.getBuyerName());
        assertThat(entity.getTaxId()).isEqualTo(invoice.getTaxId());
        assertThat(entity.getStatus()).isEqualTo(invoice.getStatus().name());
        assertThat(entity.getItems()).hasSameSizeAs(invoice.getItems());
        assertThat(entity.getItems().get(0).getTaxRate()).isEqualByComparingTo(BigDecimal.valueOf(23));
    }

    @Test
    @DisplayName("Should map InvoiceEntity back to domain Invoice correctly")
    void toDomain_shouldMapEntityToDomain() {
        // given
        BigDecimal taxRate = BigDecimal.valueOf(8);
        InvoiceItemEmbeddable item = new InvoiceItemEmbeddable("item", 2, BigDecimal.TEN, taxRate);
        InvoiceEntity entity = InvoiceEntity.builder()
                .id(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .buyerName("Buyer")
                .taxId("1234567890")
                .status(InvoiceStatus.DRAFT.name())
                .items(List.of(item))
                .build();
        
        // when
        Invoice invoice = mapper.toDomain(entity);
        
        // then
        assertThat(invoice.getId()).isEqualTo(entity.getId());
        assertThat(invoice.getOrderId()).isEqualTo(entity.getOrderId());
        assertThat(invoice.getBuyerName()).isEqualTo(entity.getBuyerName());
        assertThat(invoice.getTaxId()).isEqualTo(entity.getTaxId());
        assertThat(invoice.getStatus().name()).isEqualTo(entity.getStatus());
        assertThat(invoice.getItems()).hasSameSizeAs(entity.getItems());
        
        InvoiceItem domainItem = invoice.getItems().getFirst();
        assertThat(domainItem.name()).isEqualTo("item");
        assertThat(domainItem.taxRate().value()).isEqualByComparingTo(taxRate);
    }
}
