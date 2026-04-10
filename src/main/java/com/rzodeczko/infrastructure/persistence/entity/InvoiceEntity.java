package com.rzodeczko.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "items")
public class InvoiceEntity {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    private String taxId;
    private String buyerName;

    @Column(nullable = false)
    private String status;

    private String externalId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "invoice_items", joinColumns = @JoinColumn(name = "invoice_id"))
    private List<InvoiceItemEmbeddable> items;

    @Lob
    @Column(name = "pdf_content", columnDefinition = "LONGBLOB", insertable = false, updatable = false)
    private byte[] pdfContent;

    @Version
    private Long version;
}
