package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.PaymentGatewayDTO;
import com.vijay.petrosoft.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@ConditionalOnProperty(name = "razorpay.enabled", havingValue = "true", matchIfMissing = false)
@RequestMapping("/api/payment-gateway")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final PaymentGatewayService paymentGatewayService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentGatewayDTO> createPaymentOrder(@Valid @RequestBody PaymentGatewayDTO paymentGatewayDTO) {
        PaymentGatewayDTO result = paymentGatewayService.createPaymentOrder(paymentGatewayDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/create-order/simple")
    public ResponseEntity<PaymentGatewayDTO> createSimplePaymentOrder(
            @RequestParam Long subscriptionId,
            @RequestParam BigDecimal amount,
            @RequestParam String customerEmail,
            @RequestParam String customerPhone) {
        PaymentGatewayDTO result = paymentGatewayService.createPaymentOrder(subscriptionId, amount, customerEmail, customerPhone);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentGatewayDTO> processPayment(
            @RequestParam String paymentId,
            @RequestParam String orderId,
            @RequestParam String signature) {
        PaymentGatewayDTO result = paymentGatewayService.processPayment(paymentId, orderId, signature);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentGatewayDTO> verifyPayment(
            @RequestParam String paymentId,
            @RequestParam String orderId,
            @RequestParam String signature) {
        PaymentGatewayDTO result = paymentGatewayService.verifyPayment(paymentId, orderId, signature);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/capture")
    public ResponseEntity<PaymentGatewayDTO> capturePayment(
            @RequestParam String paymentId,
            @RequestParam BigDecimal amount) {
        PaymentGatewayDTO result = paymentGatewayService.capturePayment(paymentId, amount);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/status/payment/{paymentId}")
    public ResponseEntity<PaymentGatewayDTO> getPaymentStatus(@PathVariable String paymentId) {
        PaymentGatewayDTO result = paymentGatewayService.getPaymentStatus(paymentId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/status/order/{orderId}")
    public ResponseEntity<PaymentGatewayDTO> getOrderStatus(@PathVariable String orderId) {
        PaymentGatewayDTO result = paymentGatewayService.getOrderStatus(orderId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentGatewayDTO> processRefund(
            @RequestParam String paymentId,
            @RequestParam BigDecimal amount,
            @RequestParam String reason) {
        PaymentGatewayDTO result = paymentGatewayService.processRefund(paymentId, amount, reason);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/refund/status/{refundId}")
    public ResponseEntity<PaymentGatewayDTO> getRefundStatus(@PathVariable String refundId) {
        PaymentGatewayDTO result = paymentGatewayService.getRefundStatus(refundId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/webhook")
    public ResponseEntity<PaymentGatewayDTO> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        PaymentGatewayDTO result = paymentGatewayService.handleWebhook(payload, signature);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<Map<String, Object>> getAvailablePaymentMethods() {
        Map<String, Object> methods = paymentGatewayService.getAvailablePaymentMethods();
        return new ResponseEntity<>(methods, HttpStatus.OK);
    }

    @GetMapping("/payment-methods/{method}")
    public ResponseEntity<Map<String, Object>> getPaymentMethodDetails(@PathVariable String method) {
        Map<String, Object> details = paymentGatewayService.getPaymentMethodDetails(method);
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @PostMapping("/customers")
    public ResponseEntity<Map<String, Object>> createCustomer(
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String name) {
        Map<String, Object> customer = paymentGatewayService.createCustomer(email, phone, name);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomer(@PathVariable String customerId) {
        Map<String, Object> customer = paymentGatewayService.getCustomer(customerId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PutMapping("/customers/{customerId}")
    public ResponseEntity<Map<String, Object>> updateCustomer(
            @PathVariable String customerId,
            @RequestBody Map<String, Object> updates) {
        Map<String, Object> customer = paymentGatewayService.updateCustomer(customerId, updates);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PostMapping("/subscriptions/{subscriptionId}/payment")
    public ResponseEntity<PaymentGatewayDTO> createSubscriptionPayment(
            @PathVariable Long subscriptionId,
            @RequestParam BigDecimal amount) {
        PaymentGatewayDTO result = paymentGatewayService.createSubscriptionPayment(subscriptionId, amount);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/subscriptions/{subscriptionId}/process-payment")
    public ResponseEntity<PaymentGatewayDTO> processSubscriptionPayment(
            @PathVariable Long subscriptionId,
            @RequestParam String paymentId) {
        PaymentGatewayDTO result = paymentGatewayService.processSubscriptionPayment(subscriptionId, paymentId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/payment-link")
    public ResponseEntity<Map<String, Object>> generatePaymentLink(@Valid @RequestBody PaymentGatewayDTO paymentGatewayDTO) {
        Map<String, Object> link = paymentGatewayService.generatePaymentLink(paymentGatewayDTO);
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getPaymentAnalytics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> analytics = paymentGatewayService.getPaymentAnalytics(startDate, endDate);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}
