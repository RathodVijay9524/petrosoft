package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.PaymentDTO;
import com.vijay.petrosoft.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(payment -> new ResponseEntity<>(payment, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsBySubscriptionId(@PathVariable Long subscriptionId) {
        List<PaymentDTO> payments = paymentService.getPaymentsBySubscriptionId(subscriptionId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/subscription/{subscriptionId}/latest")
    public ResponseEntity<List<PaymentDTO>> getLatestPaymentsBySubscriptionId(
            @PathVariable Long subscriptionId,
            @RequestParam(defaultValue = "5") int limit) {
        List<PaymentDTO> payments = paymentService.getLatestPaymentsBySubscriptionId(subscriptionId, limit);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(@PathVariable String transactionId) {
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(payment -> new ResponseEntity<>(payment, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/gateway/{gatewayTransactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByGatewayTransactionId(@PathVariable String gatewayTransactionId) {
        return paymentService.getPaymentByGatewayTransactionId(gatewayTransactionId)
                .map(payment -> new ResponseEntity<>(payment, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentDTO paymentDTO) {
        PaymentDTO updatedPayment = paymentService.updatePayment(id, paymentDTO);
        return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentDTO> processPayment(
            @RequestParam Long subscriptionId,
            @RequestParam Double amount,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {
        PaymentDTO payment = paymentService.processPayment(subscriptionId, amount, paymentMethod, transactionId);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String gatewayResponse) {
        PaymentDTO updatedPayment = paymentService.updatePaymentStatus(id, status, gatewayResponse);
        return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentDTO> processRefund(
            @PathVariable Long id,
            @RequestParam String reason) {
        PaymentDTO refundedPayment = paymentService.processRefund(id, reason);
        return new ResponseEntity<>(refundedPayment, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<PaymentDTO> payments = paymentService.getPaymentsBetweenDates(startDate, endDate);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenueBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Double totalRevenue = paymentService.getTotalRevenueBetweenDates(startDate, endDate);
        return new ResponseEntity<>(totalRevenue, HttpStatus.OK);
    }

    @GetMapping("/success-count")
    public ResponseEntity<Long> getSuccessfulPaymentCountBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Long count = paymentService.getSuccessfulPaymentCountBetweenDates(startDate, endDate);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/gateway/initiate")
    public ResponseEntity<PaymentDTO> initiatePaymentGateway(
            @RequestParam Long subscriptionId,
            @RequestParam Double amount) {
        PaymentDTO payment = paymentService.initiatePaymentGateway(subscriptionId, amount);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PostMapping("/gateway/callback")
    public ResponseEntity<PaymentDTO> handlePaymentCallback(
            @RequestParam String gatewayTransactionId,
            @RequestParam String status,
            @RequestParam String response) {
        PaymentDTO payment = paymentService.handlePaymentCallback(gatewayTransactionId, status, response);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
