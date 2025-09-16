package com.vijay.petrosoft.service;

import java.util.List;

public interface SMSService {
    
    void sendSMS(String phoneNumber, String message);
    
    void sendBulkSMS(List<String> phoneNumbers, String message);
    
    void sendWelcomeSMS(String phoneNumber, String customerName);
    
    void sendOTP(String phoneNumber, String otp);
    
    void sendPaymentConfirmationSMS(String phoneNumber, String customerName, Double amount);
    
    void sendSubscriptionAlertSMS(String phoneNumber, String planName, String status);
    
    void sendLowStockAlertSMS(String phoneNumber, String fuelType, Double currentStock);
    
    void sendDailyReportSMS(String phoneNumber, String reportSummary);
    
    void sendMaintenanceAlertSMS(String phoneNumber, String message);
}
