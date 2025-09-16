package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.SubscriptionPlan;
import com.vijay.petrosoft.dto.SubscriptionPlanDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.vijay.petrosoft.repository.SubscriptionPlanRepository;
import com.vijay.petrosoft.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public SubscriptionPlanDTO createPlan(SubscriptionPlanDTO planDTO) {
        if (isPlanNameExists(planDTO.getName())) {
            throw new DuplicateResourceException("Plan name already exists: " + planDTO.getName());
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(planDTO.getName())
                .description(planDTO.getDescription())
                .price(planDTO.getPrice())
                .billingCycle(SubscriptionPlan.BillingCycle.valueOf(planDTO.getBillingCycle()))
                .active(planDTO.isActive())
                .features(planDTO.getFeatures())
                .maxPumps(planDTO.getMaxPumps())
                .maxUsers(planDTO.getMaxUsers())
                .supportIncluded(planDTO.isSupportIncluded())
                .reportingIncluded(planDTO.isReportingIncluded())
                .apiAccess(planDTO.isApiAccess())
                .build();

        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return convertToDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanDTO updatePlan(Long id, SubscriptionPlanDTO planDTO) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));

        plan.setName(planDTO.getName());
        plan.setDescription(planDTO.getDescription());
        plan.setPrice(planDTO.getPrice());
        plan.setBillingCycle(SubscriptionPlan.BillingCycle.valueOf(planDTO.getBillingCycle()));
        plan.setActive(planDTO.isActive());
        plan.setFeatures(planDTO.getFeatures());
        plan.setMaxPumps(planDTO.getMaxPumps());
        plan.setMaxUsers(planDTO.getMaxUsers());
        plan.setSupportIncluded(planDTO.isSupportIncluded());
        plan.setReportingIncluded(planDTO.isReportingIncluded());
        plan.setApiAccess(planDTO.isApiAccess());

        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return convertToDTO(updatedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlanDTO> getPlanById(Long id) {
        return subscriptionPlanRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getAllPlans() {
        return subscriptionPlanRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePlan(Long id) {
        if (!subscriptionPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription plan not found with id: " + id);
        }
        subscriptionPlanRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getActivePlans() {
        return subscriptionPlanRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getInactivePlans() {
        return subscriptionPlanRepository.findByActiveFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlanDTO activatePlan(Long id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        
        plan.setActive(true);
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return convertToDTO(updatedPlan);
    }

    @Override
    public SubscriptionPlanDTO deactivatePlan(Long id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));
        
        plan.setActive(false);
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return convertToDTO(updatedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlanDTO> getPlanByName(String name) {
        return subscriptionPlanRepository.findByName(name)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getPlansByBillingCycle(String billingCycle) {
        return subscriptionPlanRepository.findByBillingCycle(SubscriptionPlan.BillingCycle.valueOf(billingCycle))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPlanNameExists(String name) {
        return subscriptionPlanRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getAvailablePlansForUpgrade(Long currentPlanId) {
        SubscriptionPlan currentPlan = subscriptionPlanRepository.findById(currentPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Current plan not found with id: " + currentPlanId));
        
        return subscriptionPlanRepository.findByActiveTrue().stream()
                .filter(plan -> plan.getPrice() > currentPlan.getPrice())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> getAvailablePlansForDowngrade(Long currentPlanId) {
        SubscriptionPlan currentPlan = subscriptionPlanRepository.findById(currentPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Current plan not found with id: " + currentPlanId));
        
        return subscriptionPlanRepository.findByActiveTrue().stream()
                .filter(plan -> plan.getPrice() < currentPlan.getPrice())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createDefaultPlans() {
        // Create Basic Plan
        if (!isPlanNameExists("Basic Plan")) {
            SubscriptionPlanDTO basicPlan = SubscriptionPlanDTO.builder()
                    .name("Basic Plan")
                    .description("Basic petrol pump management features")
                    .price(999.0)
                    .billingCycle("MONTHLY")
                    .active(true)
                    .features(List.of("Basic Sales Management", "Customer Management", "Basic Reports"))
                    .maxPumps(1)
                    .maxUsers(3)
                    .supportIncluded(false)
                    .reportingIncluded(false)
                    .apiAccess(false)
                    .build();
            createPlan(basicPlan);
        }

        // Create Premium Plan
        if (!isPlanNameExists("Premium Plan")) {
            SubscriptionPlanDTO premiumPlan = SubscriptionPlanDTO.builder()
                    .name("Premium Plan")
                    .description("Advanced petrol pump management with reporting")
                    .price(1999.0)
                    .billingCycle("MONTHLY")
                    .active(true)
                    .features(List.of("All Basic Features", "Advanced Reports", "Inventory Management", "Email Support"))
                    .maxPumps(3)
                    .maxUsers(10)
                    .supportIncluded(true)
                    .reportingIncluded(true)
                    .apiAccess(false)
                    .build();
            createPlan(premiumPlan);
        }

        // Create Enterprise Plan
        if (!isPlanNameExists("Enterprise Plan")) {
            SubscriptionPlanDTO enterprisePlan = SubscriptionPlanDTO.builder()
                    .name("Enterprise Plan")
                    .description("Complete petrol pump management solution")
                    .price(3999.0)
                    .billingCycle("MONTHLY")
                    .active(true)
                    .features(List.of("All Premium Features", "Unlimited Pumps", "API Access", "Priority Support", "Custom Reports"))
                    .maxPumps(999)
                    .maxUsers(999)
                    .supportIncluded(true)
                    .reportingIncluded(true)
                    .apiAccess(true)
                    .build();
            createPlan(enterprisePlan);
        }
    }

    private SubscriptionPlanDTO convertToDTO(SubscriptionPlan plan) {
        return SubscriptionPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .billingCycle(plan.getBillingCycle().name())
                .active(plan.isActive())
                .features(plan.getFeatures())
                .maxPumps(plan.getMaxPumps())
                .maxUsers(plan.getMaxUsers())
                .supportIncluded(plan.isSupportIncluded())
                .reportingIncluded(plan.isReportingIncluded())
                .apiAccess(plan.isApiAccess())
                .build();
    }
}
