package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private String invoiceNumber;

    private BigDecimal amount;

    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
