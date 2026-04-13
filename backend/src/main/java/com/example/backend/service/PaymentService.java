package com.example.backend.service;

import com.example.backend.dto.PaymentRequest;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private FailedPaymentLogRepository failedPaymentLogRepository;

    public PaymentTransaction processPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice already paid");
        }

        Subscription subscription = invoice.getSubscription();

        // Simulate Gateway
        boolean isSuccess = new Random().nextBoolean(); 

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setInvoice(invoice);
        transaction.setAmount(invoice.getAmount());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setGatewayReference("TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());

        if (isSuccess) {
            transaction.setStatus(PaymentStatus.SUCCESS);
            
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);

            if (subscription.getStatus() == SubscriptionStatus.TRIALING || subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
                subscription.setStatus(SubscriptionStatus.ACTIVE);
            }
            
            LocalDateTime now = LocalDateTime.now();
            if (subscription.getPlan().getBillingCycle() == BillingCycle.MONTHLY) {
                subscription.setNextBillingDate(now.plusMonths(1));
                subscription.setCurrentPeriodEnd(now.plusMonths(1));
            } else {
                subscription.setNextBillingDate(now.plusYears(1));
                subscription.setCurrentPeriodEnd(now.plusYears(1));
            }
            subscriptionRepository.save(subscription);
            
        } else {
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setFailureReason("Insufficient funds / Gateway Error");
            
            subscription.setStatus(SubscriptionStatus.PAST_DUE);
            subscriptionRepository.save(subscription);

            FailedPaymentLog log = new FailedPaymentLog();
            log.setSubscription(subscription);
            log.setInvoice(invoice);
            log.setRetryCount(1);
            log.setNextRetryDate(LocalDateTime.now().plusDays(1)); // Day 1
            log.setStatus(FailedPaymentStatus.PENDING);
            failedPaymentLogRepository.save(log);
        }

        return paymentTransactionRepository.save(transaction);
    }
}
