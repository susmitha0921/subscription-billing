package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String description;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    private BigDecimal price;

    private String features; // comma separated or JSON

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
