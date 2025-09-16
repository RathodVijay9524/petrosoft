package com.vijay.petrosoft.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 30000) // Run every 30 seconds
    public void processPendingNotifications() {
        try {
            log.debug("Processing pending notifications...");
            notificationService.processPendingNotifications();
        } catch (Exception e) {
            log.error("Error processing pending notifications", e);
        }
    }

    @Scheduled(fixedDelay = 60000) // Run every minute
    public void processFailedNotifications() {
        try {
            log.debug("Processing failed notifications for retry...");
            notificationService.processFailedNotifications();
        } catch (Exception e) {
            log.error("Error processing failed notifications", e);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Run daily at 9 AM
    public void sendDailyReports() {
        try {
            log.info("Sending daily reports...");
            // This would typically fetch all pumps and send daily reports
            // For demo purposes, we'll send a sample report
            notificationService.sendDailySalesReport(1L, "Daily sales report generated successfully");
        } catch (Exception e) {
            log.error("Error sending daily reports", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    public void cleanupOldNotifications() {
        try {
            log.info("Cleaning up old notifications...");
            // This would typically delete notifications older than 30 days
            // Implementation would depend on business requirements
        } catch (Exception e) {
            log.error("Error cleaning up old notifications", e);
        }
    }
}
