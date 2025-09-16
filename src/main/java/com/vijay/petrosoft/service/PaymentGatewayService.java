package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.PaymentGatewayDTO;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayService {
    
    // Payment Creation
    PaymentGatewayDTO createPaymentOrder(PaymentGatewayDTO paymentGatewayDTO);
    PaymentGatewayDTO createPaymentOrder(Long subscriptionId, BigDecimal amount, String customerEmail, String customerPhone);
    
    // Payment Processing
    PaymentGatewayDTO processPayment(String paymentId, String orderId, String signature);
    PaymentGatewayDTO verifyPayment(String paymentId, String orderId, String signature);
    PaymentGatewayDTO capturePayment(String paymentId, BigDecimal amount);
    
    // Payment Status
    PaymentGatewayDTO getPaymentStatus(String paymentId);
    PaymentGatewayDTO getOrderStatus(String orderId);
    
    // Refund Processing
    PaymentGatewayDTO processRefund(String paymentId, BigDecimal amount, String reason);
    PaymentGatewayDTO getRefundStatus(String refundId);
    
    // Webhook Handling
    PaymentGatewayDTO handleWebhook(String payload, String signature);
    boolean verifyWebhookSignature(String payload, String signature);
    
    // Payment Methods
    Map<String, Object> getAvailablePaymentMethods();
    Map<String, Object> getPaymentMethodDetails(String method);
    
    // Customer Management
    Map<String, Object> createCustomer(String email, String phone, String name);
    Map<String, Object> getCustomer(String customerId);
    Map<String, Object> updateCustomer(String customerId, Map<String, Object> updates);
    
    // Subscription Integration
    PaymentGatewayDTO createSubscriptionPayment(Long subscriptionId, BigDecimal amount);
    PaymentGatewayDTO processSubscriptionPayment(Long subscriptionId, String paymentId);
    
    // Utility Methods
    Map<String, Object> generatePaymentLink(PaymentGatewayDTO paymentGatewayDTO);
    Map<String, Object> getPaymentAnalytics(String startDate, String endDate);
}
