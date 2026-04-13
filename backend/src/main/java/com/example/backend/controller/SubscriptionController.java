package com.example.backend.controller;

import com.example.backend.dto.SubscriptionRequest;
import com.example.backend.model.Subscription;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new RuntimeException("Not authenticated");
        }
        Long userId = (Long) session.getAttribute("userId");
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) throw new RuntimeException("User not found");
        return user.get();
    }

    @PostMapping
    public ResponseEntity<?> subscribe(HttpServletRequest httpRequest, @RequestBody SubscriptionRequest request) {
        try {
            User user = getAuthenticatedUser(httpRequest);
            Subscription sub = subscriptionService.subscribe(user, request.getPlanId());
            return ResponseEntity.ok(sub);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getSubscriptions(HttpServletRequest httpRequest) {
        try {
            User user = getAuthenticatedUser(httpRequest);
            // In a real app we might differentiate between ADMIN and CUSTOMER here
            // Admin sees all, customer sees own. Let's return own for simplicity unless admin.
            // Simplified: if role is CUSTOMER return user's, if ADMIN return all
            if (user.getRole().name().equals("ADMIN")) {
                return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
            } else {
                return ResponseEntity.ok(subscriptionService.getUserSubscriptions(user.getId()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscription(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Subscription> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }

    @PutMapping("/{id}/upgrade")
    public ResponseEntity<Subscription> upgrade(@PathVariable Long id, @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.upgrade(id, request.getPlanId()));
    }

    @PutMapping("/{id}/downgrade")
    public ResponseEntity<Subscription> downgrade(@PathVariable Long id, @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.downgrade(id, request.getPlanId()));
    }
}
