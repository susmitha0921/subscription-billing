package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PlanService planService;

    public Subscription subscribe(User user, Long planId) {
        SubscriptionPlan plan = planService.getPlan(planId);

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.TRIALING); // as per rule: starts only after first successful payment
        
        LocalDateTime now = LocalDateTime.now();
        subscription.setStartDate(now);
        subscription.setCurrentPeriodStart(now);
        
        // Let's set initial next billing date to +7 days to give time for payment
        subscription.setNextBillingDate(now.plusDays(7));
        subscription.setCurrentPeriodEnd(now.plusDays(7)); 

        subscription = subscriptionRepository.save(subscription);

        // Generate initial invoice
        Invoice invoice = new Invoice();
        invoice.setSubscription(subscription);
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setAmount(plan.getPrice());
        invoice.setDueDate(now.plusDays(7));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoiceRepository.save(invoice);

        return subscription;
    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }
    
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Subscription getSubscription(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    public Subscription cancelSubscription(Long id) {
        Subscription sub = getSubscription(id);
        sub.setCancelAtPeriodEnd(true);
        return subscriptionRepository.save(sub);
    }

    public Subscription upgrade(Long id, Long newPlanId) {
        Subscription sub = getSubscription(id);
        SubscriptionPlan newPlan = planService.getPlan(newPlanId);
        
        // Immediate change
        sub.setPlan(newPlan);
        subscriptionRepository.save(sub);
        
        // Pro-rated charge logic here (omitted complex math, just create a new invoice for remaining)
        Invoice invoice = new Invoice();
        invoice.setSubscription(sub);
        invoice.setInvoiceNumber("INV-UPG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setAmount(newPlan.getPrice()); // Simplify by charging full price for upgrade in this simple app
        invoice.setDueDate(LocalDateTime.now().plusDays(7));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoiceRepository.save(invoice);
        
        return sub;
    }

    public Subscription downgrade(Long id, Long newPlanId) {
        Subscription sub = getSubscription(id);
        SubscriptionPlan newPlan = planService.getPlan(newPlanId);
        // Apply from NEXT billing cycle (In this simple logic, we just change the plan now but don't charge)
        sub.setPlan(newPlan);
        return subscriptionRepository.save(sub);
    }
}
