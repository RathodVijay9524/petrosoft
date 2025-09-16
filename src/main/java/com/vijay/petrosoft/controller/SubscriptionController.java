package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.SubscriptionDTO;
import com.vijay.petrosoft.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDTO> createSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        SubscriptionDTO createdSubscription = subscriptionService.createSubscription(subscriptionDTO);
        return new ResponseEntity<>(createdSubscription, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .map(subscription -> new ResponseEntity<>(subscription, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        List<SubscriptionDTO> subscriptions = subscriptionService.getAllSubscriptions();
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByPumpId(@PathVariable Long pumpId) {
        List<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsByPumpId(pumpId);
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/active")
    public ResponseEntity<SubscriptionDTO> getActiveSubscriptionByPumpId(@PathVariable Long pumpId) {
        return subscriptionService.getActiveSubscriptionByPumpId(pumpId)
                .map(subscription -> new ResponseEntity<>(subscription, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionDTO>> getActiveSubscriptions() {
        List<SubscriptionDTO> subscriptions = subscriptionService.getActiveSubscriptions();
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<SubscriptionDTO>> getExpiredSubscriptions() {
        List<SubscriptionDTO> subscriptions = subscriptionService.getExpiredSubscriptions();
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @GetMapping("/due-for-payment")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsDueForPayment() {
        List<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsDueForPayment();
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsExpiringBetween(
            @RequestParam LocalDate startDate, 
            @RequestParam LocalDate endDate) {
        List<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsExpiringBetween(startDate, endDate);
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(@PathVariable Long id, @Valid @RequestBody SubscriptionDTO subscriptionDTO) {
        SubscriptionDTO updatedSubscription = subscriptionService.updateSubscription(id, subscriptionDTO);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<SubscriptionDTO> activateSubscription(@PathVariable Long id) {
        SubscriptionDTO updatedSubscription = subscriptionService.activateSubscription(id);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<SubscriptionDTO> deactivateSubscription(@PathVariable Long id) {
        SubscriptionDTO updatedSubscription = subscriptionService.deactivateSubscription(id);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<SubscriptionDTO> renewSubscription(@PathVariable Long id, @RequestParam LocalDate newEndDate) {
        SubscriptionDTO updatedSubscription = subscriptionService.renewSubscription(id, newEndDate);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionDTO> cancelSubscription(@PathVariable Long id) {
        SubscriptionDTO updatedSubscription = subscriptionService.cancelSubscription(id);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<SubscriptionDTO> processPayment(
            @PathVariable Long id,
            @RequestParam Double amount,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {
        SubscriptionDTO updatedSubscription = subscriptionService.processPayment(id, amount, paymentMethod, transactionId);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<SubscriptionDTO> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        SubscriptionDTO updatedSubscription = subscriptionService.updatePaymentStatus(id, status);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveSubscriptionCount() {
        Long count = subscriptionService.getActiveSubscriptionCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByPaymentStatus(@PathVariable String status) {
        List<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsByPaymentStatus(status);
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @PutMapping("/{id}/upgrade")
    public ResponseEntity<SubscriptionDTO> upgradePlan(@PathVariable Long id, @RequestParam Long newPlanId) {
        SubscriptionDTO updatedSubscription = subscriptionService.upgradePlan(id, newPlanId);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @PutMapping("/{id}/downgrade")
    public ResponseEntity<SubscriptionDTO> downgradePlan(@PathVariable Long id, @RequestParam Long newPlanId) {
        SubscriptionDTO updatedSubscription = subscriptionService.downgradePlan(id, newPlanId);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
