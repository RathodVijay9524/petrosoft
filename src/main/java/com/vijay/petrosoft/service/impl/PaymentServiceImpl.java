package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Payment;
import com.vijay.petrosoft.domain.Subscription;
import com.vijay.petrosoft.dto.PaymentDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.BusinessLogicException;
import com.vijay.petrosoft.repository.PaymentRepository;
import com.vijay.petrosoft.repository.SubscriptionRepository;
import com.vijay.petrosoft.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Subscription subscription = subscriptionRepository.findById(paymentDTO.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + paymentDTO.getSubscriptionId()));

        Payment payment = Payment.builder()
                .subscription(subscription)
                .amount(paymentDTO.getAmount())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .transactionId(paymentDTO.getTransactionId())
                .status(Payment.PaymentStatus.valueOf(paymentDTO.getStatus()))
                .paymentDate(paymentDTO.getPaymentDate() != null ? paymentDTO.getPaymentDate() : LocalDateTime.now())
                .notes(paymentDTO.getNotes())
                .gatewayResponse(paymentDTO.getGatewayResponse())
                .gatewayTransactionId(paymentDTO.getGatewayTransactionId())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    @Override
    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setTransactionId(paymentDTO.getTransactionId());
        payment.setStatus(Payment.PaymentStatus.valueOf(paymentDTO.getStatus()));
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setNotes(paymentDTO.getNotes());
        payment.setGatewayResponse(paymentDTO.getGatewayResponse());
        payment.setGatewayTransactionId(paymentDTO.getGatewayTransactionId());

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToDTO(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsBySubscriptionId(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(Payment.PaymentStatus.valueOf(status.toUpperCase())).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentByGatewayTransactionId(String gatewayTransactionId) {
        return paymentRepository.findByGatewayTransactionId(gatewayTransactionId)
                .map(this::convertToDTO);
    }

    @Override
    public PaymentDTO processPayment(Long subscriptionId, Double amount, String paymentMethod, String transactionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        // Check if transaction ID already exists
        if (transactionId != null && paymentRepository.findByTransactionId(transactionId).isPresent()) {
            throw new BusinessLogicException("Transaction ID already exists: " + transactionId);
        }

        Payment payment = Payment.builder()
                .subscription(subscription)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .transactionId(transactionId)
                .status(Payment.PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    @Override
    public PaymentDTO updatePaymentStatus(Long paymentId, String status, String gatewayResponse) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
            payment.setStatus(paymentStatus);
            payment.setGatewayResponse(gatewayResponse);
            
            if (paymentStatus == Payment.PaymentStatus.COMPLETED) {
                payment.setPaymentDate(LocalDateTime.now());
            }
            
            Payment updatedPayment = paymentRepository.save(payment);
            return convertToDTO(updatedPayment);
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException("Invalid payment status: " + status);
        }
    }

    @Override
    public PaymentDTO processRefund(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BusinessLogicException("Only completed payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setNotes(payment.getNotes() + " | Refund Reason: " + reason);
        payment.setGatewayResponse(payment.getGatewayResponse() + " | Refunded on: " + LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToDTO(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findPaymentsBetweenDates(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        Double totalRevenue = paymentRepository.getTotalRevenueBetweenDates(startDate, endDate);
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSuccessfulPaymentCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.countSuccessfulPaymentsBetweenDates(startDate, endDate);
    }

    @Override
    public PaymentDTO initiatePaymentGateway(Long subscriptionId, Double amount) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        // Generate a temporary transaction ID for gateway processing
        String gatewayTransactionId = "GATEWAY_" + System.currentTimeMillis();
        
        Payment payment = Payment.builder()
                .subscription(subscription)
                .amount(amount)
                .paymentMethod("GATEWAY")
                .status(Payment.PaymentStatus.PENDING)
                .gatewayTransactionId(gatewayTransactionId)
                .paymentDate(LocalDateTime.now())
                .notes("Payment initiated via gateway")
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    @Override
    public PaymentDTO handlePaymentCallback(String gatewayTransactionId, String status, String response) {
        Payment payment = paymentRepository.findByGatewayTransactionId(gatewayTransactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with gateway transaction ID: " + gatewayTransactionId));

        payment.setGatewayResponse(response);
        
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
            payment.setStatus(paymentStatus);
            
            if (paymentStatus == Payment.PaymentStatus.COMPLETED) {
                payment.setPaymentDate(LocalDateTime.now());
            }
            
            Payment updatedPayment = paymentRepository.save(payment);
            return convertToDTO(updatedPayment);
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException("Invalid payment status from gateway: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getLatestPaymentsBySubscriptionId(Long subscriptionId, int limit) {
        return paymentRepository.findLatestPaymentsBySubscriptionId(subscriptionId).stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .subscriptionId(payment.getSubscription().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus().name())
                .paymentDate(payment.getPaymentDate())
                .notes(payment.getNotes())
                .gatewayResponse(payment.getGatewayResponse())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .build();
    }
}
