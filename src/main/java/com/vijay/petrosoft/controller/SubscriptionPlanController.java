package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.SubscriptionPlanDTO;
import com.vijay.petrosoft.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @PostMapping
    public ResponseEntity<SubscriptionPlanDTO> createPlan(@Valid @RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlanDTO createdPlan = subscriptionPlanService.createPlan(planDTO);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> getPlanById(@PathVariable Long id) {
        return subscriptionPlanService.getPlanById(id)
                .map(plan -> new ResponseEntity<>(plan, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllPlans();
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionPlanDTO>> getActivePlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getActivePlans();
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<SubscriptionPlanDTO>> getInactivePlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getInactivePlans();
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/billing-cycle/{billingCycle}")
    public ResponseEntity<List<SubscriptionPlanDTO>> getPlansByBillingCycle(@PathVariable String billingCycle) {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getPlansByBillingCycle(billingCycle);
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SubscriptionPlanDTO> getPlanByName(@PathVariable String name) {
        return subscriptionPlanService.getPlanByName(name)
                .map(plan -> new ResponseEntity<>(plan, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> updatePlan(@PathVariable Long id, @Valid @RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlanDTO updatedPlan = subscriptionPlanService.updatePlan(id, planDTO);
        return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<SubscriptionPlanDTO> activatePlan(@PathVariable Long id) {
        SubscriptionPlanDTO updatedPlan = subscriptionPlanService.activatePlan(id);
        return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<SubscriptionPlanDTO> deactivatePlan(@PathVariable Long id) {
        SubscriptionPlanDTO updatedPlan = subscriptionPlanService.deactivatePlan(id);
        return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionPlanService.deletePlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/setup-default-plans")
    public ResponseEntity<String> setupDefaultPlans() {
        subscriptionPlanService.createDefaultPlans();
        return new ResponseEntity<>("Default subscription plans created successfully", HttpStatus.OK);
    }

    @GetMapping("/upgrade-options/{currentPlanId}")
    public ResponseEntity<List<SubscriptionPlanDTO>> getUpgradeOptions(@PathVariable Long currentPlanId) {
        List<SubscriptionPlanDTO> upgradeOptions = subscriptionPlanService.getAvailablePlansForUpgrade(currentPlanId);
        return new ResponseEntity<>(upgradeOptions, HttpStatus.OK);
    }

    @GetMapping("/downgrade-options/{currentPlanId}")
    public ResponseEntity<List<SubscriptionPlanDTO>> getDowngradeOptions(@PathVariable Long currentPlanId) {
        List<SubscriptionPlanDTO> downgradeOptions = subscriptionPlanService.getAvailablePlansForDowngrade(currentPlanId);
        return new ResponseEntity<>(downgradeOptions, HttpStatus.OK);
    }
}
