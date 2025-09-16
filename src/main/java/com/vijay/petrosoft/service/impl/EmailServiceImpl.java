package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String message, String attachmentPath) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message);

            // Add attachment
            File file = new File(attachmentPath);
            if (file.exists()) {
                helper.addAttachment(file.getName(), file);
            }

            mailSender.send(mimeMessage);
            log.info("Email with attachment sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment to: {}", to, e);
            throw new RuntimeException("Failed to send email with attachment: " + e.getMessage());
        }
    }

    @Override
    public void sendBulkEmail(List<String> recipients, String subject, String message) {
        for (String recipient : recipients) {
            try {
                sendEmail(recipient, subject, message);
            } catch (Exception e) {
                log.error("Failed to send bulk email to: {}", recipient, e);
                // Continue with other recipients
            }
        }
        log.info("Bulk email sending completed for {} recipients", recipients.size());
    }

    @Override
    public void sendWelcomeEmail(String to, String customerName) {
        String subject = "Welcome to Petrosoft!";
        String htmlContent = generateWelcomeEmailHtml(customerName);
        sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Password Reset - Petrosoft";
        String message = String.format(
            "You requested a password reset. Please use the following token: %s\n\n" +
            "This token will expire in 24 hours.\n\n" +
            "If you did not request this reset, please ignore this email.\n\n" +
            "Best regards,\nPetrosoft Team", resetToken);
        sendEmail(to, subject, message);
    }

    @Override
    public void sendSubscriptionNotification(String to, String planName, String status) {
        String subject = "Subscription " + status + " - Petrosoft";
        String message = String.format(
            "Your subscription for %s plan has been %s.\n\n" +
            "Plan: %s\n" +
            "Status: %s\n\n" +
            "Thank you for using Petrosoft!\n\n" +
            "Best regards,\nPetrosoft Team", planName, status, planName, status);
        sendEmail(to, subject, message);
    }

    @Override
    public void sendPaymentNotification(String to, String customerName, Double amount, String status) {
        String subject = "Payment " + status + " - Petrosoft";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your payment of ₹%.2f has been %s.\n\n" +
            "Amount: ₹%.2f\n" +
            "Status: %s\n" +
            "Date: %s\n\n" +
            "Thank you for your business!\n\n" +
            "Best regards,\nPetrosoft Team", 
            customerName, amount, status, amount, status, java.time.LocalDateTime.now());
        sendEmail(to, subject, message);
    }

    @Override
    public void sendReportEmail(String to, String reportType, String reportData) {
        String subject = reportType + " Report - Petrosoft";
        String message = String.format(
            "Please find attached your %s report.\n\n" +
            "Report Type: %s\n" +
            "Generated on: %s\n\n" +
            "If you have any questions, please contact our support team.\n\n" +
            "Best regards,\nPetrosoft Team", 
            reportType, reportType, java.time.LocalDateTime.now());
        sendEmail(to, subject, message);
    }

    private String generateWelcomeEmailHtml(String customerName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to Petrosoft</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { padding: 20px; text-align: center; background-color: #34495e; color: white; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Petrosoft!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Welcome to Petrosoft - your comprehensive fuel station management solution!</p>
                        <p>We're excited to have you on board. With Petrosoft, you can:</p>
                        <ul>
                            <li>Manage your fuel station operations efficiently</li>
                            <li>Track sales and inventory in real-time</li>
                            <li>Generate comprehensive reports</li>
                            <li>Handle customer relationships</li>
                            <li>Monitor subscription and billing</li>
                        </ul>
                        <p style="text-align: center;">
                            <a href="http://localhost:8080" class="button">Get Started</a>
                        </p>
                        <p>If you have any questions or need assistance, our support team is here to help.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Petrosoft. All rights reserved.</p>
                        <p>Best regards,<br>The Petrosoft Team</p>
                    </div>
                </div>
            </body>
            </html>
            """, customerName);
    }
}
