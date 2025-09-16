package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.NotificationDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotificationService {
    
    // CRUD Operations
    NotificationDTO createNotification(NotificationDTO notificationDTO);
    NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO);
    Optional<NotificationDTO> getNotificationById(Long id);
    List<NotificationDTO> getAllNotifications();
    List<NotificationDTO> getNotificationsByPumpId(Long pumpId);
    List<NotificationDTO> getNotificationsByUserId(Long userId);
    void deleteNotification(Long id);
    
    // Notification Sending
    NotificationDTO sendEmailNotification(String recipientEmail, String subject, String message);
    NotificationDTO sendSMSNotification(String recipientPhone, String message);
    NotificationDTO sendSystemNotification(Long pumpId, String title, String message);
    
    // Template-based Notifications
    NotificationDTO sendWelcomeEmail(String recipientEmail, String customerName);
    NotificationDTO sendSubscriptionExpiryNotification(Long subscriptionId);
    NotificationDTO sendPaymentSuccessNotification(String recipientEmail, String customerName, Double amount);
    NotificationDTO sendPaymentFailureNotification(String recipientEmail, String customerName, String reason);
    NotificationDTO sendLowStockAlert(Long pumpId, String fuelType, Double currentStock);
    NotificationDTO sendDailySalesReport(Long pumpId, String reportData);
    
    // Bulk Notifications
    List<NotificationDTO> sendBulkEmailNotifications(List<String> recipients, String subject, String message);
    List<NotificationDTO> sendBulkSMSNotifications(List<String> phoneNumbers, String message);
    
    // Notification Management
    List<NotificationDTO> getPendingNotifications();
    List<NotificationDTO> getFailedNotifications();
    NotificationDTO retryNotification(Long notificationId);
    void processPendingNotifications();
    void processFailedNotifications();
    
    // Status Management
    NotificationDTO markAsSent(Long notificationId);
    NotificationDTO markAsFailed(Long notificationId, String errorMessage);
    NotificationDTO cancelNotification(Long notificationId);
    
    // Analytics and Reporting
    Map<String, Object> getNotificationAnalytics();
    Map<String, Object> getNotificationAnalyticsByPumpId(Long pumpId);
    List<NotificationDTO> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Template Management
    String processTemplate(String templateName, Map<String, Object> variables);
    NotificationDTO scheduleNotification(NotificationDTO notificationDTO, LocalDateTime scheduledTime);
}
