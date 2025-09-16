package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.PaymentDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    
    // Payment CRUD Operations
    PaymentDTO createPayment(PaymentDTO paymentDTO);
    PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO);
    Optional<PaymentDTO> getPaymentById(Long id);
    List<PaymentDTO> getAllPayments();
    void deletePayment(Long id);
    
    // Payment Queries
    List<PaymentDTO> getPaymentsBySubscriptionId(Long subscriptionId);
    List<PaymentDTO> getPaymentsByStatus(String status);
    Optional<PaymentDTO> getPaymentByTransactionId(String transactionId);
    Optional<PaymentDTO> getPaymentByGatewayTransactionId(String gatewayTransactionId);
    
    // Payment Processing
    PaymentDTO processPayment(Long subscriptionId, Double amount, String paymentMethod, String transactionId);
    PaymentDTO updatePaymentStatus(Long paymentId, String status, String gatewayResponse);
    PaymentDTO processRefund(Long paymentId, String reason);
    
    // Payment Analytics
    List<PaymentDTO> getPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    Double getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    Long getSuccessfulPaymentCountBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    // Gateway Integration
    PaymentDTO initiatePaymentGateway(Long subscriptionId, Double amount);
    PaymentDTO handlePaymentCallback(String gatewayTransactionId, String status, String response);
    
    // Payment History
    List<PaymentDTO> getLatestPaymentsBySubscriptionId(Long subscriptionId, int limit);
}
