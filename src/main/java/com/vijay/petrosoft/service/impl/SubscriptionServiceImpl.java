package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Subscription;
import com.vijay.petrosoft.domain.SubscriptionPlan;
import com.vijay.petrosoft.dto.SubscriptionDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.BusinessLogicException;
import com.vijay.petrosoft.repository.SubscriptionRepository;
import com.vijay.petrosoft.repository.SubscriptionPlanRepository;
import com.vijay.petrosoft.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        // Check if pump already has an active subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findActiveSubscriptionByPumpId(subscriptionDTO.getPumpId());
        if (existingSubscription.isPresent()) {
            throw new DuplicateResourceException("Pump already has an active subscription");
        }

        // Get the subscription plan
        SubscriptionPlan plan = null;
        if (subscriptionDTO.getPlanName() != null) {
            plan = subscriptionPlanRepository.findByName(subscriptionDTO.getPlanName())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found: " + subscriptionDTO.getPlanName()));
        }

        Subscription subscription = Subscription.builder()
                .pumpId(subscriptionDTO.getPumpId())
                .plan(plan)
                .planName(subscriptionDTO.getPlanName())
                .startsAt(subscriptionDTO.getStartsAt())
                .endsAt(subscriptionDTO.getEndsAt())
                .active(subscriptionDTO.isActive())
                .amount(subscriptionDTO.getAmount())
                .paymentStatus(Subscription.PaymentStatus.PENDING)
                .billingCycle(Subscription.BillingCycle.valueOf(subscriptionDTO.getBillingCycle()))
                .description(subscriptionDTO.getDescription())
                .build();

        // Calculate next payment date
        subscription.setNextPaymentDate(calculateNextPaymentDate(subscription.getStartsAt(), subscription.getBillingCycle()));

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(savedSubscription);
    }

    @Override
    public SubscriptionDTO updateSubscription(Long id, SubscriptionDTO subscriptionDTO) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        subscription.setPumpId(subscriptionDTO.getPumpId());
        subscription.setPlanName(subscriptionDTO.getPlanName());
        subscription.setStartsAt(subscriptionDTO.getStartsAt());
        subscription.setEndsAt(subscriptionDTO.getEndsAt());
        subscription.setActive(subscriptionDTO.isActive());
        subscription.setAmount(subscriptionDTO.getAmount());
        subscription.setBillingCycle(Subscription.BillingCycle.valueOf(subscriptionDTO.getBillingCycle()));
        subscription.setDescription(subscriptionDTO.getDescription());

        // Update next payment date if billing cycle changed
        subscription.setNextPaymentDate(calculateNextPaymentDate(subscription.getStartsAt(), subscription.getBillingCycle()));

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscriptionDTO> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsByPumpId(Long pumpId) {
        return subscriptionRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscriptionDTO> getActiveSubscriptionByPumpId(Long pumpId) {
        return subscriptionRepository.findActiveSubscriptionByPumpId(pumpId)
                .map(this::convertToDTO);
    }

    @Override
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getActiveSubscriptions() {
        return subscriptionRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getExpiredSubscriptions() {
        return subscriptionRepository.findExpiredSubscriptions(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsDueForPayment() {
        return subscriptionRepository.findSubscriptionsDueForPayment(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return subscriptionRepository.findSubscriptionsExpiringBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDTO activateSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        subscription.setActive(true);
        subscription.setPaymentStatus(Subscription.PaymentStatus.COMPLETED);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Override
    public SubscriptionDTO deactivateSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        subscription.setActive(false);
        subscription.setPaymentStatus(Subscription.PaymentStatus.CANCELLED);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Override
    public SubscriptionDTO renewSubscription(Long id, LocalDate newEndDate) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        if (newEndDate.isBefore(LocalDate.now())) {
            throw new BusinessLogicException("New end date cannot be in the past");
        }

        subscription.setEndsAt(newEndDate);
        subscription.setActive(true);
        subscription.setPaymentStatus(Subscription.PaymentStatus.COMPLETED);
        subscription.setNextPaymentDate(calculateNextPaymentDate(LocalDate.now(), subscription.getBillingCycle()));

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        subscription.setActive(false);
        subscription.setPaymentStatus(Subscription.PaymentStatus.CANCELLED);
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Override
    public SubscriptionDTO processPayment(Long subscriptionId, Double amount, String paymentMethod, String transactionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        subscription.setPaymentStatus(Subscription.PaymentStatus.COMPLETED);
        subscription.setLastPaymentDate(LocalDate.now());
        subscription.setNextPaymentDate(calculateNextPaymentDate(LocalDate.now(), subscription.getBillingCycle()));

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Override
    public SubscriptionDTO updatePaymentStatus(Long subscriptionId, String status) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        try {
            Subscription.PaymentStatus paymentStatus = Subscription.PaymentStatus.valueOf(status.toUpperCase());
            subscription.setPaymentStatus(paymentStatus);
            
            if (paymentStatus == Subscription.PaymentStatus.COMPLETED) {
                subscription.setLastPaymentDate(LocalDate.now());
                subscription.setNextPaymentDate(calculateNextPaymentDate(LocalDate.now(), subscription.getBillingCycle()));
            }
            
            Subscription updatedSubscription = subscriptionRepository.save(subscription);
            return convertToDTO(updatedSubscription);
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException("Invalid payment status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveSubscriptionCount() {
        return subscriptionRepository.countActiveSubscriptions();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsByPaymentStatus(String status) {
        try {
            Subscription.PaymentStatus paymentStatus = Subscription.PaymentStatus.valueOf(status.toUpperCase());
            return subscriptionRepository.findByPaymentStatus(paymentStatus).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException("Invalid payment status: " + status);
        }
    }

    @Override
    public SubscriptionDTO upgradePlan(Long subscriptionId, Long newPlanId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        SubscriptionPlan newPlan = subscriptionPlanRepository.findById(newPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + newPlanId));

        if (newPlan.getPrice() <= subscription.getAmount()) {
            throw new BusinessLogicException("New plan price must be higher than current plan for upgrade");
        }

        subscription.setPlan(newPlan);
        subscription.setPlanName(newPlan.getName());
        subscription.setAmount(newPlan.getPrice());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Override
    public SubscriptionDTO downgradePlan(Long subscriptionId, Long newPlanId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        SubscriptionPlan newPlan = subscriptionPlanRepository.findById(newPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + newPlanId));

        if (newPlan.getPrice() >= subscription.getAmount()) {
            throw new BusinessLogicException("New plan price must be lower than current plan for downgrade");
        }

        subscription.setPlan(newPlan);
        subscription.setPlanName(newPlan.getName());
        subscription.setAmount(newPlan.getPrice());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    private LocalDate calculateNextPaymentDate(LocalDate currentDate, Subscription.BillingCycle billingCycle) {
        switch (billingCycle) {
            case MONTHLY:
                return currentDate.plusMonths(1);
            case QUARTERLY:
                return currentDate.plusMonths(3);
            case ANNUAL:
                return currentDate.plusYears(1);
            default:
                return currentDate.plusMonths(1);
        }
    }

    private SubscriptionDTO convertToDTO(Subscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .pumpId(subscription.getPumpId())
                .planName(subscription.getPlanName())
                .startsAt(subscription.getStartsAt())
                .endsAt(subscription.getEndsAt())
                .active(subscription.isActive())
                .amount(subscription.getAmount())
                .paymentStatus(subscription.getPaymentStatus() != null ? subscription.getPaymentStatus().name() : null)
                .lastPaymentDate(subscription.getLastPaymentDate())
                .nextPaymentDate(subscription.getNextPaymentDate())
                .billingCycle(subscription.getBillingCycle() != null ? subscription.getBillingCycle().name() : null)
                .description(subscription.getDescription())
                .build();
    }
}
