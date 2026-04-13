package com.example.backend.dto;

import com.example.backend.model.BillingCycle;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlanRequest {
    private String name;
    private String description;
    private BillingCycle billingCycle;
    private BigDecimal price;
    private String features;
}
