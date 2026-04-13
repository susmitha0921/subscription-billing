package com.example.backend.dto;

import com.example.backend.model.PaymentMethodType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long invoiceId;
    private PaymentMethodType paymentMethod;
}
