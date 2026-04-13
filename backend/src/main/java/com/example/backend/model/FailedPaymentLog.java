package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "failed_payment_log")
public class FailedPaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    private Integer retryCount = 0;

    private LocalDateTime nextRetryDate;

    @Enumerated(EnumType.STRING)
    private FailedPaymentStatus status;
}
