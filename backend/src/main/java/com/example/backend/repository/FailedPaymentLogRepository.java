package com.example.backend.repository;

import com.example.backend.model.FailedPaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedPaymentLogRepository extends JpaRepository<FailedPaymentLog, Long> {
    List<FailedPaymentLog> findBySubscriptionId(Long subscriptionId);
}
