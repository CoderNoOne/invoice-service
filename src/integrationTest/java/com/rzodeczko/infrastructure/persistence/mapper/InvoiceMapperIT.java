package com.rzodeczko.infrastructure.persistence.mapper;

import com.rzodeczko.domain.model.Invoice;
import com.rzodeczko.domain.model.InvoiceItem;
import com.rzodeczko.domain.model.InvoiceStatus;
import com.rzodeczko.infrastructure.persistence.entity.InvoiceEntity;
import com.rzodeczko.infrastructure.persistence.entity.InvoiceItemEmbeddable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
@Import(InvoiceMapper.class)
@ActiveProfiles("integration-test")
class InvoiceMapperIT {

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Test
    void toEntity_shouldConvertDomainToEntity() {
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        List<InvoiceItem> items = List.of(
                new InvoiceItem("Item1", 1, BigDecimal.TEN),
                new InvoiceItem("Item2", 2, BigDecimal.valueOf(20))
        );
        Invoice domain = Invoice.restore(id, orderId, "123-456", "John Doe", "ext-123", InvoiceStatus.ISSUED, items);

        InvoiceEntity entity = invoiceMapper.toEntity(domain);

        assertEquals(id, entity.getId());
        assertEquals(orderId, entity.getOrderId());
        assertEquals("123-456", entity.getTaxId());
        assertEquals("John Doe", entity.getBuyerName());
        assertEquals("ext-123", entity.getExternalId());
        assertEquals("ISSUED", entity.getStatus());
        assertEquals(2, entity.getItems().size());
        assertEquals("Item1", entity.getItems().get(0).getName());
        assertEquals(1, entity.getItems().get(0).getQuantity());
    }

    @Test
    void toDomain_shouldConvertEntityToDomain() {
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        List<InvoiceItemEmbeddable> items = List.of(
                new InvoiceItemEmbeddable("Item1", 1, BigDecimal.TEN)
        );
        InvoiceEntity entity = InvoiceEntity.builder()
                .id(id)
                .orderId(orderId)
                .taxId("123-456")
                .buyerName("John Doe")
                .externalId("ext-123")
                .status("ISSUED")
                .items(items)
                .build();

        Invoice domain = invoiceMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals(orderId, domain.getOrderId());
        assertEquals("123-456", domain.getTaxId());
        assertEquals("John Doe", domain.getBuyerName());
        assertEquals("ext-123", domain.getExternalId());
        assertEquals(InvoiceStatus.ISSUED, domain.getStatus());
        assertEquals(1, domain.getItems().size());
        assertEquals("Item1", domain.getItems().get(0).name());
    }

    @Test
    void roundTripConversion_shouldPreserveData() {
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        List<InvoiceItem> items = List.of(
                new InvoiceItem("Item1", 1, BigDecimal.TEN)
        );
        Invoice originalDomain = Invoice.restore(id, orderId, "123", "Buyer", "ext-1", InvoiceStatus.DRAFT, items);

        InvoiceEntity entity = invoiceMapper.toEntity(originalDomain);
        Invoice restoredDomain = invoiceMapper.toDomain(entity);

        assertEquals(originalDomain.getId(), restoredDomain.getId());
        assertEquals(originalDomain.getOrderId(), restoredDomain.getOrderId());
        assertEquals(originalDomain.getTaxId(), restoredDomain.getTaxId());
        assertEquals(originalDomain.getBuyerName(), restoredDomain.getBuyerName());
        assertEquals(originalDomain.getExternalId(), restoredDomain.getExternalId());
        assertEquals(originalDomain.getStatus(), restoredDomain.getStatus());
    }
}
