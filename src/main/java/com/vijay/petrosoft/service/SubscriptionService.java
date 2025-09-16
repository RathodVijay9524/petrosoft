package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.SubscriptionDTO;
import com.vijay.petrosoft.dto.SubscriptionPlanDTO;
import com.vijay.petrosoft.domain.Subscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    
    // Subscription Management
    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);
    SubscriptionDTO updateSubscription(Long id, SubscriptionDTO subscriptionDTO);
    Optional<SubscriptionDTO> getSubscriptionById(Long id);
    List<SubscriptionDTO> getAllSubscriptions();
    List<SubscriptionDTO> getSubscriptionsByPumpId(Long pumpId);
    Optional<SubscriptionDTO> getActiveSubscriptionByPumpId(Long pumpId);
    void deleteSubscription(Long id);
    
    // Subscription Status Management
    List<SubscriptionDTO> getActiveSubscriptions();
    List<SubscriptionDTO> getExpiredSubscriptions();
    List<SubscriptionDTO> getSubscriptionsDueForPayment();
    List<SubscriptionDTO> getSubscriptionsExpiringBetween(LocalDate startDate, LocalDate endDate);
    
    // Business Logic
    SubscriptionDTO activateSubscription(Long id);
    SubscriptionDTO deactivateSubscription(Long id);
    SubscriptionDTO renewSubscription(Long id, LocalDate newEndDate);
    SubscriptionDTO cancelSubscription(Long id);
    
    // Payment Integration
    SubscriptionDTO processPayment(Long subscriptionId, Double amount, String paymentMethod, String transactionId);
    SubscriptionDTO updatePaymentStatus(Long subscriptionId, String status);
    
    // Analytics
    Long getActiveSubscriptionCount();
    List<SubscriptionDTO> getSubscriptionsByPaymentStatus(String status);
    
    // Plan Management
    SubscriptionDTO upgradePlan(Long subscriptionId, Long newPlanId);
    SubscriptionDTO downgradePlan(Long subscriptionId, Long newPlanId);
}
