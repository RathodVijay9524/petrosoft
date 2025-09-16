package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.NotificationDTO;
import com.vijay.petrosoft.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
        return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(notification -> new ResponseEntity<>(notification, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByPumpId(@PathVariable Long pumpId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByPumpId(pumpId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<NotificationDTO>> getPendingNotifications() {
        List<NotificationDTO> notifications = notificationService.getPendingNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<NotificationDTO>> getFailedNotifications() {
        List<NotificationDTO> notifications = notificationService.getFailedNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getNotificationAnalytics() {
        Map<String, Object> analytics = notificationService.getNotificationAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/analytics/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getNotificationAnalyticsByPumpId(@PathVariable Long pumpId) {
        Map<String, Object> analytics = notificationService.getNotificationAnalyticsByPumpId(pumpId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByDateRange(startDate, endDate);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PostMapping("/send-email")
    public ResponseEntity<NotificationDTO> sendEmailNotification(
            @RequestParam String recipientEmail,
            @RequestParam String subject,
            @RequestParam String message) {
        NotificationDTO notification = notificationService.sendEmailNotification(recipientEmail, subject, message);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/send-sms")
    public ResponseEntity<NotificationDTO> sendSMSNotification(
            @RequestParam String recipientPhone,
            @RequestParam String message) {
        NotificationDTO notification = notificationService.sendSMSNotification(recipientPhone, message);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/send-system")
    public ResponseEntity<NotificationDTO> sendSystemNotification(
            @RequestParam Long pumpId,
            @RequestParam String title,
            @RequestParam String message) {
        NotificationDTO notification = notificationService.sendSystemNotification(pumpId, title, message);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/welcome-email")
    public ResponseEntity<NotificationDTO> sendWelcomeEmail(
            @RequestParam String recipientEmail,
            @RequestParam String customerName) {
        NotificationDTO notification = notificationService.sendWelcomeEmail(recipientEmail, customerName);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/subscription-expiry/{subscriptionId}")
    public ResponseEntity<NotificationDTO> sendSubscriptionExpiryNotification(@PathVariable Long subscriptionId) {
        NotificationDTO notification = notificationService.sendSubscriptionExpiryNotification(subscriptionId);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/payment-success")
    public ResponseEntity<NotificationDTO> sendPaymentSuccessNotification(
            @RequestParam String recipientEmail,
            @RequestParam String customerName,
            @RequestParam Double amount) {
        NotificationDTO notification = notificationService.sendPaymentSuccessNotification(recipientEmail, customerName, amount);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/payment-failure")
    public ResponseEntity<NotificationDTO> sendPaymentFailureNotification(
            @RequestParam String recipientEmail,
            @RequestParam String customerName,
            @RequestParam String reason) {
        NotificationDTO notification = notificationService.sendPaymentFailureNotification(recipientEmail, customerName, reason);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/low-stock-alert")
    public ResponseEntity<NotificationDTO> sendLowStockAlert(
            @RequestParam Long pumpId,
            @RequestParam String fuelType,
            @RequestParam Double currentStock) {
        NotificationDTO notification = notificationService.sendLowStockAlert(pumpId, fuelType, currentStock);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/daily-sales-report")
    public ResponseEntity<NotificationDTO> sendDailySalesReport(
            @RequestParam Long pumpId,
            @RequestParam String reportData) {
        NotificationDTO notification = notificationService.sendDailySalesReport(pumpId, reportData);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/bulk-email")
    public ResponseEntity<List<NotificationDTO>> sendBulkEmailNotifications(
            @RequestBody List<String> recipients,
            @RequestParam String subject,
            @RequestParam String message) {
        List<NotificationDTO> notifications = notificationService.sendBulkEmailNotifications(recipients, subject, message);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PostMapping("/bulk-sms")
    public ResponseEntity<List<NotificationDTO>> sendBulkSMSNotifications(
            @RequestBody List<String> phoneNumbers,
            @RequestParam String message) {
        List<NotificationDTO> notifications = notificationService.sendBulkSMSNotifications(phoneNumbers, message);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<NotificationDTO> retryNotification(@PathVariable Long id) {
        NotificationDTO notification = notificationService.retryNotification(id);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/{id}/mark-sent")
    public ResponseEntity<NotificationDTO> markAsSent(@PathVariable Long id) {
        NotificationDTO notification = notificationService.markAsSent(id);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/{id}/mark-failed")
    public ResponseEntity<NotificationDTO> markAsFailed(
            @PathVariable Long id,
            @RequestParam String errorMessage) {
        NotificationDTO notification = notificationService.markAsFailed(id, errorMessage);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<NotificationDTO> cancelNotification(@PathVariable Long id) {
        NotificationDTO notification = notificationService.cancelNotification(id);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping("/process-pending")
    public ResponseEntity<Void> processPendingNotifications() {
        notificationService.processPendingNotifications();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/process-failed")
    public ResponseEntity<Void> processFailedNotifications() {
        notificationService.processFailedNotifications();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/schedule")
    public ResponseEntity<NotificationDTO> scheduleNotification(
            @Valid @RequestBody NotificationDTO notificationDTO,
            @RequestParam LocalDateTime scheduledTime) {
        NotificationDTO notification = notificationService.scheduleNotification(notificationDTO, scheduledTime);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationDTO notificationDTO) {
        NotificationDTO updatedNotification = notificationService.updateNotification(id, notificationDTO);
        return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
