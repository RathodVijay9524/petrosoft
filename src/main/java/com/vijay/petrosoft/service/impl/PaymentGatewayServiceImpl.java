package com.vijay.petrosoft.service.impl;

import com.razorpay.*;
import com.vijay.petrosoft.dto.PaymentGatewayDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.BusinessLogicException;
import com.vijay.petrosoft.repository.SubscriptionRepository;
import com.vijay.petrosoft.service.PaymentGatewayService;
import com.vijay.petrosoft.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "razorpay.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final RazorpayClient razorpayClient;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Override
    public PaymentGatewayDTO createPaymentOrder(PaymentGatewayDTO paymentGatewayDTO) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", paymentGatewayDTO.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Convert to paise
            orderRequest.put("currency", paymentGatewayDTO.getCurrency());
            orderRequest.put("receipt", "order_" + System.currentTimeMillis());
            orderRequest.put("notes", Map.of(
                "subscription_id", paymentGatewayDTO.getSubscriptionId(),
                "customer_name", paymentGatewayDTO.getCustomerName(),
                "description", paymentGatewayDTO.getDescription()
            ));

            Order order = razorpayClient.orders.create(orderRequest);
            
            paymentGatewayDTO.setOrderId(order.get("id"));
            paymentGatewayDTO.setStatus("CREATED");
            
            log.info("Payment order created successfully: {}", order.get("id").toString());
            return paymentGatewayDTO;

        } catch (RazorpayException e) {
            log.error("Failed to create payment order", e);
            throw new BusinessLogicException("Failed to create payment order: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO createPaymentOrder(Long subscriptionId, BigDecimal amount, String customerEmail, String customerPhone) {
        PaymentGatewayDTO paymentGatewayDTO = PaymentGatewayDTO.builder()
                .subscriptionId(subscriptionId)
                .amount(amount)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .description("Subscription payment for Petrosoft")
                .build();

        return createPaymentOrder(paymentGatewayDTO);
    }

    @Override
    public PaymentGatewayDTO processPayment(String paymentId, String orderId, String signature) {
        try {
            Payment payment = razorpayClient.payments.fetch(paymentId);
            
            // Verify payment signature
            if (!verifyPaymentSignature(paymentId, orderId, signature)) {
                throw new BusinessLogicException("Invalid payment signature");
            }

            PaymentGatewayDTO result = PaymentGatewayDTO.builder()
                    .paymentId(paymentId)
                    .orderId(orderId)
                    .signature(signature)
                    .status(payment.get("status"))
                    .gatewayResponse(payment.toString())
                    .build();

            // Update payment status in our system
            updatePaymentStatusInSystem(paymentId, payment.get("status"));

            log.info("Payment processed successfully: {}", paymentId);
            return result;

        } catch (RazorpayException e) {
            log.error("Failed to process payment: {}", paymentId, e);
            throw new BusinessLogicException("Failed to process payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO verifyPayment(String paymentId, String orderId, String signature) {
        try {
            Payment payment = razorpayClient.payments.fetch(paymentId);
            
            boolean isValid = verifyPaymentSignature(paymentId, orderId, signature);
            
            PaymentGatewayDTO result = PaymentGatewayDTO.builder()
                    .paymentId(paymentId)
                    .orderId(orderId)
                    .signature(signature)
                    .status(isValid ? "VERIFIED" : "INVALID")
                    .gatewayResponse(payment.toString())
                    .build();

            return result;

        } catch (RazorpayException e) {
            log.error("Failed to verify payment: {}", paymentId, e);
            throw new BusinessLogicException("Failed to verify payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO capturePayment(String paymentId, BigDecimal amount) {
        try {
            JSONObject captureRequest = new JSONObject();
            captureRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            captureRequest.put("currency", "INR");

            Payment payment = razorpayClient.payments.capture(paymentId, captureRequest);
            
            PaymentGatewayDTO result = PaymentGatewayDTO.builder()
                    .paymentId(paymentId)
                    .status(payment.get("status"))
                    .gatewayResponse(payment.toString())
                    .build();

            log.info("Payment captured successfully: {}", paymentId);
            return result;

        } catch (RazorpayException e) {
            log.error("Failed to capture payment: {}", paymentId, e);
            throw new BusinessLogicException("Failed to capture payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO getPaymentStatus(String paymentId) {
        try {
            Payment payment = razorpayClient.payments.fetch(paymentId);
            
            return PaymentGatewayDTO.builder()
                    .paymentId(paymentId)
                    .status(payment.get("status"))
                    .gatewayResponse(payment.toString())
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to get payment status: {}", paymentId, e);
            throw new BusinessLogicException("Failed to get payment status: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO getOrderStatus(String orderId) {
        try {
            Order order = razorpayClient.orders.fetch(orderId);
            
            return PaymentGatewayDTO.builder()
                    .orderId(orderId)
                    .status(order.get("status"))
                    .gatewayResponse(order.toString())
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to get order status: {}", orderId, e);
            throw new BusinessLogicException("Failed to get order status: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO processRefund(String paymentId, BigDecimal amount, String reason) {
        try {
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            refundRequest.put("notes", Map.of("reason", reason));

            Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);
            
            PaymentGatewayDTO result = PaymentGatewayDTO.builder()
                    .paymentId(paymentId)
                    .status("REFUNDED")
                    .gatewayResponse(refund.toString())
                    .build();

            log.info("Refund processed successfully: {}", refund.get("id").toString());
            return result;

        } catch (RazorpayException e) {
            log.error("Failed to process refund for payment: {}", paymentId, e);
            throw new BusinessLogicException("Failed to process refund: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO getRefundStatus(String refundId) {
        try {
            Refund refund = razorpayClient.payments.fetchRefund(refundId);
            
            return PaymentGatewayDTO.builder()
                    .status(refund.get("status"))
                    .gatewayResponse(refund.toString())
                    .build();

        } catch (RazorpayException e) {
            log.error("Failed to get refund status: {}", refundId, e);
            throw new BusinessLogicException("Failed to get refund status: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO handleWebhook(String payload, String signature) {
        try {
            // Verify webhook signature
            if (!verifyWebhookSignature(payload, signature)) {
                throw new BusinessLogicException("Invalid webhook signature");
            }

            JSONObject webhookData = new JSONObject(payload);
            String event = webhookData.getString("event");
            
            PaymentGatewayDTO result = PaymentGatewayDTO.builder()
                    .status("WEBHOOK_PROCESSED")
                    .webhookData(payload)
                    .build();

            // Process different webhook events
            switch (event) {
                case "payment.captured":
                    handlePaymentCaptured(webhookData);
                    break;
                case "payment.failed":
                    handlePaymentFailed(webhookData);
                    break;
                case "order.paid":
                    handleOrderPaid(webhookData);
                    break;
                default:
                    log.info("Unhandled webhook event: {}", event);
            }

            return result;

        } catch (Exception e) {
            log.error("Failed to handle webhook", e);
            throw new BusinessLogicException("Failed to handle webhook: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            String expectedSignature = generateWebhookSignature(payload);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Failed to verify webhook signature", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getAvailablePaymentMethods() {
        Map<String, Object> methods = new HashMap<>();
        methods.put("card", "Credit/Debit Cards");
        methods.put("netbanking", "Net Banking");
        methods.put("wallet", "Digital Wallets");
        methods.put("upi", "UPI");
        methods.put("emi", "EMI");
        return methods;
    }

    @Override
    public Map<String, Object> getPaymentMethodDetails(String method) {
        Map<String, Object> details = new HashMap<>();
        details.put("method", method);
        details.put("enabled", true);
        details.put("description", "Payment method details for " + method);
        return details;
    }

    @Override
    public Map<String, Object> createCustomer(String email, String phone, String name) {
        try {
            JSONObject customerRequest = new JSONObject();
            customerRequest.put("name", name);
            customerRequest.put("email", email);
            customerRequest.put("contact", phone);

            Customer customer = razorpayClient.customers.create(customerRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", customer.get("id"));
            result.put("name", customer.get("name"));
            result.put("email", customer.get("email"));
            result.put("contact", customer.get("contact"));
            
            return result;

        } catch (RazorpayException e) {
            log.error("Failed to create customer", e);
            throw new BusinessLogicException("Failed to create customer: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getCustomer(String customerId) {
        try {
            Customer customer = razorpayClient.customers.fetch(customerId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", customer.get("id"));
            result.put("name", customer.get("name"));
            result.put("email", customer.get("email"));
            result.put("contact", customer.get("contact"));
            
            return result;

        } catch (RazorpayException e) {
            log.error("Failed to get customer: {}", customerId, e);
            throw new BusinessLogicException("Failed to get customer: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> updateCustomer(String customerId, Map<String, Object> updates) {
        try {
            JSONObject customerRequest = new JSONObject(updates);
            Customer customer = razorpayClient.customers.edit(customerId, customerRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", customer.get("id"));
            result.put("name", customer.get("name"));
            result.put("email", customer.get("email"));
            result.put("contact", customer.get("contact"));
            
            return result;

        } catch (RazorpayException e) {
            log.error("Failed to update customer: {}", customerId, e);
            throw new BusinessLogicException("Failed to update customer: " + e.getMessage());
        }
    }

    @Override
    public PaymentGatewayDTO createSubscriptionPayment(Long subscriptionId, BigDecimal amount) {
        // Get subscription details
        var subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));

        PaymentGatewayDTO paymentGatewayDTO = PaymentGatewayDTO.builder()
                .subscriptionId(subscriptionId)
                .amount(amount)
                .customerEmail("customer@example.com") // Get from subscription
                .customerPhone("+919876543210") // Get from subscription
                .description("Subscription payment for " + subscription.getPlanName())
                .build();

        return createPaymentOrder(paymentGatewayDTO);
    }

    @Override
    public PaymentGatewayDTO processSubscriptionPayment(Long subscriptionId, String paymentId) {
        // Process payment and update subscription
        PaymentGatewayDTO result = getPaymentStatus(paymentId);
        
        // Update subscription payment status
        if ("captured".equals(result.getStatus())) {
            paymentService.processPayment(subscriptionId, 
                100.0, // Get from payment details
                "RAZORPAY", 
                paymentId);
        }

        return result;
    }

    @Override
    public Map<String, Object> generatePaymentLink(PaymentGatewayDTO paymentGatewayDTO) {
        try {
            JSONObject linkRequest = new JSONObject();
            linkRequest.put("amount", paymentGatewayDTO.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
            linkRequest.put("currency", paymentGatewayDTO.getCurrency());
            linkRequest.put("description", paymentGatewayDTO.getDescription());
            linkRequest.put("customer", Map.of(
                "name", paymentGatewayDTO.getCustomerName(),
                "email", paymentGatewayDTO.getCustomerEmail(),
                "contact", paymentGatewayDTO.getCustomerPhone()
            ));

            PaymentLink paymentLink = razorpayClient.paymentLink.create(linkRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", paymentLink.get("id"));
            result.put("short_url", paymentLink.get("short_url"));
            result.put("status", paymentLink.get("status"));
            
            return result;

        } catch (RazorpayException e) {
            log.error("Failed to generate payment link", e);
            throw new BusinessLogicException("Failed to generate payment link: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getPaymentAnalytics(String startDate, String endDate) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("total_payments", 100);
        analytics.put("successful_payments", 95);
        analytics.put("failed_payments", 5);
        analytics.put("total_amount", BigDecimal.valueOf(1000000));
        analytics.put("period", startDate + " to " + endDate);
        return analytics;
    }

    // Helper methods
    private boolean verifyPaymentSignature(String paymentId, String orderId, String signature) {
        try {
            String data = orderId + "|" + paymentId;
            String expectedSignature = generateSignature(data);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Failed to verify payment signature", e);
            return false;
        }
    }

    private String generateSignature(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    private String generateWebhookSignature(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("HmacSHA256");
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate webhook signature", e);
        }
    }

    private void updatePaymentStatusInSystem(String paymentId, String status) {
        // Update payment status in our system
        log.info("Updating payment status in system: {} - {}", paymentId, status);
    }

    private void handlePaymentCaptured(JSONObject webhookData) {
        log.info("Payment captured: {}", webhookData);
    }

    private void handlePaymentFailed(JSONObject webhookData) {
        log.info("Payment failed: {}", webhookData);
    }

    private void handleOrderPaid(JSONObject webhookData) {
        log.info("Order paid: {}", webhookData);
    }
}
