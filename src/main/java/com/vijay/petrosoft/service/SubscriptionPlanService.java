package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.SubscriptionPlanDTO;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanService {
    
    // Plan CRUD Operations
    SubscriptionPlanDTO createPlan(SubscriptionPlanDTO planDTO);
    SubscriptionPlanDTO updatePlan(Long id, SubscriptionPlanDTO planDTO);
    Optional<SubscriptionPlanDTO> getPlanById(Long id);
    List<SubscriptionPlanDTO> getAllPlans();
    void deletePlan(Long id);
    
    // Plan Status Management
    List<SubscriptionPlanDTO> getActivePlans();
    List<SubscriptionPlanDTO> getInactivePlans();
    SubscriptionPlanDTO activatePlan(Long id);
    SubscriptionPlanDTO deactivatePlan(Long id);
    
    // Plan Queries
    Optional<SubscriptionPlanDTO> getPlanByName(String name);
    List<SubscriptionPlanDTO> getPlansByBillingCycle(String billingCycle);
    boolean isPlanNameExists(String name);
    
    // Business Logic
    List<SubscriptionPlanDTO> getAvailablePlansForUpgrade(Long currentPlanId);
    List<SubscriptionPlanDTO> getAvailablePlansForDowngrade(Long currentPlanId);
    
    // Default Plans Setup
    void createDefaultPlans();
}
