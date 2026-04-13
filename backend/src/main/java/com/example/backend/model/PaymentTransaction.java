package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String gatewayReference;
    
    private String failureReason;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
