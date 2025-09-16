package com.vijay.petrosoft.service;

import java.util.List;

public interface EmailService {
    
    void sendEmail(String to, String subject, String message);
    
    void sendHtmlEmail(String to, String subject, String htmlContent);
    
    void sendEmailWithAttachment(String to, String subject, String message, String attachmentPath);
    
    void sendBulkEmail(List<String> recipients, String subject, String message);
    
    void sendWelcomeEmail(String to, String customerName);
    
    void sendPasswordResetEmail(String to, String resetToken);
    
    void sendSubscriptionNotification(String to, String planName, String status);
    
    void sendPaymentNotification(String to, String customerName, Double amount, String status);
    
    void sendReportEmail(String to, String reportType, String reportData);
}
