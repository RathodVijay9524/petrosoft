package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    List<SubscriptionPlan> findByActiveTrue();
    
    List<SubscriptionPlan> findByActiveFalse();
    
    Optional<SubscriptionPlan> findByName(String name);
    
    List<SubscriptionPlan> findByBillingCycle(SubscriptionPlan.BillingCycle billingCycle);
    
    boolean existsByName(String name);
}
