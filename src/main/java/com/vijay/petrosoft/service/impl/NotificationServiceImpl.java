package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Notification;
import com.vijay.petrosoft.dto.NotificationDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.repository.NotificationRepository;
import com.vijay.petrosoft.service.NotificationService;
import com.vijay.petrosoft.service.EmailService;
import com.vijay.petrosoft.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SMSService smsService;

    @Override
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = Notification.builder()
                .title(notificationDTO.getTitle())
                .message(notificationDTO.getMessage())
                .type(Notification.NotificationType.valueOf(notificationDTO.getType()))
                .status(Notification.NotificationStatus.PENDING)
                .recipientEmail(notificationDTO.getRecipientEmail())
                .recipientPhone(notificationDTO.getRecipientPhone())
                .pumpId(notificationDTO.getPumpId())
                .userId(notificationDTO.getUserId())
                .templateName(notificationDTO.getTemplateName())
                .templateData(notificationDTO.getTemplateData() != null ? 
                    notificationDTO.getTemplateData().toString() : null)
                .scheduledAt(notificationDTO.getScheduledAt())
                .retryCount(0)
                .maxRetries(notificationDTO.getMaxRetries() != null ? 
                    notificationDTO.getMaxRetries() : 3)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    @Override
    public NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(Notification.NotificationType.valueOf(notificationDTO.getType()));
        notification.setRecipientEmail(notificationDTO.getRecipientEmail());
        notification.setRecipientPhone(notificationDTO.getRecipientPhone());
        notification.setPumpId(notificationDTO.getPumpId());
        notification.setUserId(notificationDTO.getUserId());
        notification.setTemplateName(notificationDTO.getTemplateName());
        notification.setTemplateData(notificationDTO.getTemplateData() != null ? 
            notificationDTO.getTemplateData().toString() : null);
        notification.setScheduledAt(notificationDTO.getScheduledAt());

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByPumpId(Long pumpId) {
        return notificationRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    public NotificationDTO sendEmailNotification(String recipientEmail, String subject, String message) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title(subject)
                .message(message)
                .type("EMAIL")
                .recipientEmail(recipientEmail)
                .status("PENDING")
                .build();

        NotificationDTO createdNotification = createNotification(notificationDTO);
        
        try {
            emailService.sendEmail(recipientEmail, subject, message);
            return markAsSent(createdNotification.getId());
        } catch (Exception e) {
            log.error("Failed to send email notification", e);
            return markAsFailed(createdNotification.getId(), e.getMessage());
        }
    }

    @Override
    public NotificationDTO sendSMSNotification(String recipientPhone, String message) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title("SMS Notification")
                .message(message)
                .type("SMS")
                .recipientPhone(recipientPhone)
                .status("PENDING")
                .build();

        NotificationDTO createdNotification = createNotification(notificationDTO);
        
        try {
            smsService.sendSMS(recipientPhone, message);
            return markAsSent(createdNotification.getId());
        } catch (Exception e) {
            log.error("Failed to send SMS notification", e);
            return markAsFailed(createdNotification.getId(), e.getMessage());
        }
    }

    @Override
    public NotificationDTO sendSystemNotification(Long pumpId, String title, String message) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title(title)
                .message(message)
                .type("SYSTEM")
                .pumpId(pumpId)
                .status("PENDING")
                .build();

        return createNotification(notificationDTO);
    }

    @Override
    public NotificationDTO sendWelcomeEmail(String recipientEmail, String customerName) {
        String subject = "Welcome to Petrosoft!";
        String message = processTemplate("welcome-email", Map.of(
            "customerName", customerName,
            "baseUrl", "http://localhost:8080"
        ));
        
        return sendEmailNotification(recipientEmail, subject, message);
    }

    @Override
    public NotificationDTO sendSubscriptionExpiryNotification(Long subscriptionId) {
        // This would typically fetch subscription details from database
        String subject = "Subscription Expiring Soon";
        String message = "Your subscription will expire in 7 days. Please renew to continue using our services.";
        
        // For demo purposes, using a default email
        return sendEmailNotification("customer@example.com", subject, message);
    }

    @Override
    public NotificationDTO sendPaymentSuccessNotification(String recipientEmail, String customerName, Double amount) {
        String subject = "Payment Successful - Petrosoft";
        String message = processTemplate("payment-success", Map.of(
            "customerName", customerName,
            "amount", amount,
            "date", LocalDateTime.now().toString()
        ));
        
        return sendEmailNotification(recipientEmail, subject, message);
    }

    @Override
    public NotificationDTO sendPaymentFailureNotification(String recipientEmail, String customerName, String reason) {
        String subject = "Payment Failed - Petrosoft";
        String message = processTemplate("payment-failure", Map.of(
            "customerName", customerName,
            "reason", reason,
            "date", LocalDateTime.now().toString()
        ));
        
        return sendEmailNotification(recipientEmail, subject, message);
    }

    @Override
    public NotificationDTO sendLowStockAlert(Long pumpId, String fuelType, Double currentStock) {
        String subject = "Low Stock Alert - " + fuelType;
        String message = String.format("Low stock alert for %s. Current stock: %.2f liters", fuelType, currentStock);
        
        return sendSystemNotification(pumpId, subject, message);
    }

    @Override
    public NotificationDTO sendDailySalesReport(Long pumpId, String reportData) {
        String subject = "Daily Sales Report";
        String message = "Daily sales report: " + reportData;
        
        return sendSystemNotification(pumpId, subject, message);
    }

    @Override
    public List<NotificationDTO> sendBulkEmailNotifications(List<String> recipients, String subject, String message) {
        return recipients.stream()
                .map(recipient -> sendEmailNotification(recipient, subject, message))
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> sendBulkSMSNotifications(List<String> phoneNumbers, String message) {
        return phoneNumbers.stream()
                .map(phone -> sendSMSNotification(phone, message))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getPendingNotifications() {
        return notificationRepository.findPendingNotifications(LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getFailedNotifications() {
        return notificationRepository.findFailedNotificationsForRetry().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO retryNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (notification.getRetryCount() >= notification.getMaxRetries()) {
            throw new RuntimeException("Maximum retry attempts exceeded");
        }

        notification.setRetryCount(notification.getRetryCount() + 1);
        notification.setStatus(Notification.NotificationStatus.PENDING);
        
        Notification updatedNotification = notificationRepository.save(notification);
        
        // Retry sending the notification
        try {
            switch (notification.getType()) {
                case EMAIL:
                    emailService.sendEmail(notification.getRecipientEmail(), notification.getTitle(), notification.getMessage());
                    break;
                case SMS:
                    smsService.sendSMS(notification.getRecipientPhone(), notification.getMessage());
                    break;
            }
            return markAsSent(notificationId);
        } catch (Exception e) {
            return markAsFailed(notificationId, e.getMessage());
        }
    }

    @Override
    public void processPendingNotifications() {
        List<NotificationDTO> pendingNotifications = getPendingNotifications();
        for (NotificationDTO notification : pendingNotifications) {
            try {
                switch (notification.getType()) {
                    case "EMAIL":
                        emailService.sendEmail(notification.getRecipientEmail(), notification.getTitle(), notification.getMessage());
                        markAsSent(notification.getId());
                        break;
                    case "SMS":
                        smsService.sendSMS(notification.getRecipientPhone(), notification.getMessage());
                        markAsSent(notification.getId());
                        break;
                }
            } catch (Exception e) {
                markAsFailed(notification.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void processFailedNotifications() {
        List<NotificationDTO> failedNotifications = getFailedNotifications();
        for (NotificationDTO notification : failedNotifications) {
            retryNotification(notification.getId());
        }
    }

    @Override
    public NotificationDTO markAsSent(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    public NotificationDTO markAsFailed(Long notificationId, String errorMessage) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notification.setStatus(Notification.NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    public NotificationDTO cancelNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notification.setStatus(Notification.NotificationStatus.CANCELLED);

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalNotifications", notificationRepository.count());
        analytics.put("sentNotifications", notificationRepository.countByStatus(Notification.NotificationStatus.SENT));
        analytics.put("pendingNotifications", notificationRepository.countByStatus(Notification.NotificationStatus.PENDING));
        analytics.put("failedNotifications", notificationRepository.countByStatus(Notification.NotificationStatus.FAILED));
        analytics.put("emailNotifications", notificationRepository.countByType(Notification.NotificationType.EMAIL));
        analytics.put("smsNotifications", notificationRepository.countByType(Notification.NotificationType.SMS));
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationAnalyticsByPumpId(Long pumpId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalNotifications", notificationRepository.countSentNotificationsByPumpId(pumpId));
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // This would require a custom query in the repository
        return notificationRepository.findAll().stream()
                .filter(n -> n.getCreatedAt().isAfter(startDate) && n.getCreatedAt().isBefore(endDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String processTemplate(String templateName, Map<String, Object> variables) {
        // Simple template processing - in production, use a proper template engine like Thymeleaf
        String template = getTemplate(templateName);
        
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
        }
        
        return template;
    }

    @Override
    public NotificationDTO scheduleNotification(NotificationDTO notificationDTO, LocalDateTime scheduledTime) {
        notificationDTO.setScheduledAt(scheduledTime);
        return createNotification(notificationDTO);
    }

    private String getTemplate(String templateName) {
        // Simple template storage - in production, use database or file system
        Map<String, String> templates = new HashMap<>();
        templates.put("welcome-email", 
            "Dear {{customerName}},\n\nWelcome to Petrosoft! We're excited to have you on board.\n\n" +
            "Visit our platform at {{baseUrl}} to get started.\n\nBest regards,\nPetrosoft Team");
        
        templates.put("payment-success",
            "Dear {{customerName}},\n\nYour payment of ₹{{amount}} has been processed successfully on {{date}}.\n\n" +
            "Thank you for your business!\n\nPetrosoft Team");
        
        templates.put("payment-failure",
            "Dear {{customerName}},\n\nYour payment failed on {{date}} due to: {{reason}}\n\n" +
            "Please try again or contact support.\n\nPetrosoft Team");
        
        return templates.getOrDefault(templateName, "Default template");
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType().toString())
                .status(notification.getStatus().toString())
                .recipientEmail(notification.getRecipientEmail())
                .recipientPhone(notification.getRecipientPhone())
                .pumpId(notification.getPumpId())
                .userId(notification.getUserId())
                .templateName(notification.getTemplateName())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .errorMessage(notification.getErrorMessage())
                .retryCount(notification.getRetryCount())
                .maxRetries(notification.getMaxRetries())
                .build();
    }
}
