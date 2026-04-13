package com.example.backend.service;

import com.example.backend.dto.PlanRequest;
import com.example.backend.model.SubscriptionPlan;
import com.example.backend.repository.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    @Autowired
    private SubscriptionPlanRepository planRepository;

    public SubscriptionPlan createPlan(PlanRequest request) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setBillingCycle(request.getBillingCycle());
        plan.setPrice(request.getPrice());
        plan.setFeatures(request.getFeatures());
        plan.setIsActive(true);
        return planRepository.save(plan);
    }

    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public SubscriptionPlan updatePlan(Long id, PlanRequest request) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setBillingCycle(request.getBillingCycle());
        plan.setPrice(request.getPrice());
        plan.setFeatures(request.getFeatures());
        return planRepository.save(plan);
    }
    
    public SubscriptionPlan getPlan(Long id) {
        return planRepository.findById(id).orElseThrow(() -> new RuntimeException("Plan not found"));
    }
}
