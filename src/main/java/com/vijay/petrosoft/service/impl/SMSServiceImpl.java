package com.vijay.petrosoft.service.impl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.vijay.petrosoft.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSServiceImpl implements SMSService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio SMS service initialized");
    }

    @Override
    public void sendSMS(String phoneNumber, String message) {
        try {
            // Ensure phone number starts with + if not already
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+" + phoneNumber;
            }

            Message twilioMessage = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(fromNumber),
                    message
            ).create();

            log.info("SMS sent successfully to: {} with SID: {}", phoneNumber, twilioMessage.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", phoneNumber, e);
            throw new RuntimeException("Failed to send SMS: " + e.getMessage());
        }
    }

    @Override
    public void sendBulkSMS(List<String> phoneNumbers, String message) {
        for (String phoneNumber : phoneNumbers) {
            try {
                sendSMS(phoneNumber, message);
                // Add small delay to avoid rate limiting
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("Failed to send bulk SMS to: {}", phoneNumber, e);
                // Continue with other phone numbers
            }
        }
        log.info("Bulk SMS sending completed for {} recipients", phoneNumbers.size());
    }

    @Override
    public void sendWelcomeSMS(String phoneNumber, String customerName) {
        String message = String.format(
            "Welcome to Petrosoft, %s! Your account has been created successfully. " +
            "You can now access our fuel station management platform. " +
            "Visit: http://localhost:8080", customerName);
        sendSMS(phoneNumber, message);
    }

    @Override
    public void sendOTP(String phoneNumber, String otp) {
        String message = String.format(
            "Your Petrosoft OTP is: %s. This OTP is valid for 5 minutes. " +
            "Do not share this OTP with anyone.", otp);
        sendSMS(phoneNumber, message);
    }

    @Override
    public void sendPaymentConfirmationSMS(String phoneNumber, String customerName, Double amount) {
        String message = String.format(
            "Hi %s, your payment of ₹%.2f has been processed successfully. " +
            "Thank you for using Petrosoft!", customerName, amount);
        sendSMS(phoneNumber, message);
    }

    @Override
    public void sendSubscriptionAlertSMS(String phoneNumber, String planName, String status) {
        String message = String.format(
            "Your %s subscription has been %s. " +
            "Thank you for using Petrosoft services!", planName, status);
        sendSMS(phoneNumber, message);
    }

    @Override
    public void sendLowStockAlertSMS(String phoneNumber, String fuelType, Double currentStock) {
        String message = String.format(
            "ALERT: Low stock for %s. Current stock: %.2f liters. " +
            "Please refill soon to avoid stock-out.", fuelType, currentStock);
        sendSMS(phoneNumber, message);
    }

    @Override
    public void sendDailyReportSMS(String phoneNumber, String reportSummary) {
        String message = String.format(
            "Daily Report: %s. " +
            "View detailed report at: http://localhost:8080/dashboard", reportSummary);
        sendSMS(phoneNumber, message);
    }

    @Override
    public void sendMaintenanceAlertSMS(String phoneNumber, String message) {
        String alertMessage = String.format(
            "Maintenance Alert: %s. " +
            "Please take necessary action. Contact support if needed.", message);
        sendSMS(phoneNumber, alertMessage);
    }
}
