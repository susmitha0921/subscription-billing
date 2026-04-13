package com.example.backend.controller;

import com.example.backend.dto.PlanRequest;
import com.example.backend.model.SubscriptionPlan;
import com.example.backend.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    @Autowired
    private PlanService planService;

    @PostMapping
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody PlanRequest request) {
        return ResponseEntity.ok(planService.createPlan(request));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlan>> getPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> updatePlan(@PathVariable Long id, @RequestBody PlanRequest request) {
        return ResponseEntity.ok(planService.updatePlan(id, request));
    }
}
