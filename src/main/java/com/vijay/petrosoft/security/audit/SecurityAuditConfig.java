package com.vijay.petrosoft.security.audit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import java.time.Duration;

/**
 * Security Audit Configuration
 * Provides comprehensive security monitoring and audit capabilities
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditConfig {

    private final MeterRegistry meterRegistry;
    
    // Security Metrics
    private final AtomicInteger failedLoginAttempts = new AtomicInteger(0);
    private final AtomicInteger successfulLogins = new AtomicInteger(0);
    private final AtomicInteger blockedRequests = new AtomicInteger(0);
    private final AtomicInteger suspiciousActivities = new AtomicInteger(0);
    private final AtomicLong totalSecurityEvents = new AtomicLong(0);
    
    // Security Events Tracking
    private final Map<String, AtomicInteger> failedLoginByUser = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failedLoginByIP = new ConcurrentHashMap<>();
    private final List<SecurityEvent> recentSecurityEvents = new ArrayList<>();
    
    // OWASP Top 10 Tracking
    private final Map<OWASPCategory, AtomicInteger> owaspViolations = new ConcurrentHashMap<>();

    /**
     * Security Health Indicator
     */
    @Bean
    public HealthIndicator securityHealthIndicator() {
        return () -> {
            try {
                int totalLogins = successfulLogins.get() + failedLoginAttempts.get();
                double failureRate = totalLogins > 0 ? 
                    (double) failedLoginAttempts.get() / totalLogins : 0.0;
                
                int recentSuspiciousActivities = getRecentSuspiciousActivities();
                
                if (failureRate > 0.3) { // 30% failure rate threshold
                    return Health.down()
                        .withDetail("login_failure_rate", failureRate)
                        .withDetail("failed_logins", failedLoginAttempts.get())
                        .withDetail("suspicious_activities", recentSuspiciousActivities)
                        .build();
                } else if (recentSuspiciousActivities > 10) {
                    return Health.down()
                        .withDetail("suspicious_activities", recentSuspiciousActivities)
                        .withDetail("login_failure_rate", failureRate)
                        .build();
                } else {
                    return Health.up()
                        .withDetail("login_failure_rate", failureRate)
                        .withDetail("failed_logins", failedLoginAttempts.get())
                        .withDetail("successful_logins", successfulLogins.get())
                        .withDetail("blocked_requests", blockedRequests.get())
                        .withDetail("suspicious_activities", recentSuspiciousActivities)
                        .build();
                }
            } catch (Exception e) {
                log.error("Error checking security health", e);
                return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
            }
        };
    }

    /**
     * Register Security Metrics
     */
    @Bean
    public String registerSecurityMetrics() {
        log.info("Security metrics configuration initialized");
        return "security-metrics-registered";
    }

    /**
     * Scheduled security monitoring - DISABLED for startup performance
     */
    // @Scheduled(fixedRate = 60000) // DISABLED - Only run when explicitly enabled
    public void monitorSecurity() {
        try {
            int totalLogins = successfulLogins.get() + failedLoginAttempts.get();
            double failureRate = totalLogins > 0 ? 
                (double) failedLoginAttempts.get() / totalLogins : 0.0;
            
            log.info("Security Metrics - Failed Logins: {}, Successful Logins: {}, " +
                    "Blocked Requests: {}, Suspicious Activities: {}, Failure Rate: {:.2f}%",
                failedLoginAttempts.get(), successfulLogins.get(), 
                blockedRequests.get(), suspiciousActivities.get(), failureRate * 100);

            // Check for suspicious patterns
            checkForSuspiciousPatterns();
            
            // Clean up old security events
            cleanOldSecurityEvents();

        } catch (Exception e) {
            log.error("Error during security monitoring", e);
        }
    }

    /**
     * Record Failed Login Attempt
     */
    public void recordFailedLogin(String username, String ipAddress) {
        failedLoginAttempts.incrementAndGet();
        totalSecurityEvents.incrementAndGet();
        
        failedLoginByUser.computeIfAbsent(username, k -> new AtomicInteger(0)).incrementAndGet();
        failedLoginByIP.computeIfAbsent(ipAddress, k -> new AtomicInteger(0)).incrementAndGet();
        
        SecurityEvent event = SecurityEvent.builder()
            .type(SecurityEventType.FAILED_LOGIN)
            .username(username)
            .ipAddress(ipAddress)
            .timestamp(Instant.now())
            .severity(SecuritySeverity.MEDIUM)
            .build();
            
        addSecurityEvent(event);
        
        log.warn("Failed login attempt for user: {} from IP: {}", username, ipAddress);
    }

    /**
     * Record Successful Login
     */
    public void recordSuccessfulLogin(String username, String ipAddress) {
        successfulLogins.incrementAndGet();
        totalSecurityEvents.incrementAndGet();
        
        SecurityEvent event = SecurityEvent.builder()
            .type(SecurityEventType.SUCCESSFUL_LOGIN)
            .username(username)
            .ipAddress(ipAddress)
            .timestamp(Instant.now())
            .severity(SecuritySeverity.LOW)
            .build();
            
        addSecurityEvent(event);
        
        log.info("Successful login for user: {} from IP: {}", username, ipAddress);
    }

    /**
     * Record Blocked Request
     */
    public void recordBlockedRequest(String ipAddress, String reason, String endpoint) {
        blockedRequests.incrementAndGet();
        totalSecurityEvents.incrementAndGet();
        
        SecurityEvent event = SecurityEvent.builder()
            .type(SecurityEventType.BLOCKED_REQUEST)
            .ipAddress(ipAddress)
            .endpoint(endpoint)
            .description(reason)
            .timestamp(Instant.now())
            .severity(SecuritySeverity.HIGH)
            .build();
            
        addSecurityEvent(event);
        
        log.warn("Blocked request from IP: {} to endpoint: {} - Reason: {}", ipAddress, endpoint, reason);
    }

    /**
     * Record Suspicious Activity
     */
    public void recordSuspiciousActivity(String username, String ipAddress, String activity, SecuritySeverity severity) {
        suspiciousActivities.incrementAndGet();
        totalSecurityEvents.incrementAndGet();
        
        SecurityEvent event = SecurityEvent.builder()
            .type(SecurityEventType.SUSPICIOUS_ACTIVITY)
            .username(username)
            .ipAddress(ipAddress)
            .description(activity)
            .timestamp(Instant.now())
            .severity(severity)
            .build();
            
        addSecurityEvent(event);
        
        log.warn("Suspicious activity detected - User: {}, IP: {}, Activity: {}", username, ipAddress, activity);
    }

    /**
     * Record OWASP Violation
     */
    public void recordOWASPViolation(OWASPCategory category, String description, String ipAddress) {
        owaspViolations.computeIfAbsent(category, k -> new AtomicInteger(0)).incrementAndGet();
        totalSecurityEvents.incrementAndGet();
        
        SecurityEvent event = SecurityEvent.builder()
            .type(SecurityEventType.OWASP_VIOLATION)
            .ipAddress(ipAddress)
            .description(description)
            .owaspCategory(category)
            .timestamp(Instant.now())
            .severity(SecuritySeverity.HIGH)
            .build();
            
        addSecurityEvent(event);
        
        log.warn("OWASP violation detected - Category: {}, Description: {}, IP: {}", 
            category, description, ipAddress);
    }

    /**
     * Add Security Event
     */
    private synchronized void addSecurityEvent(SecurityEvent event) {
        recentSecurityEvents.add(event);
        
        // Keep only last 1000 events
        if (recentSecurityEvents.size() > 1000) {
            recentSecurityEvents.remove(0);
        }
    }

    /**
     * Check for Suspicious Patterns
     */
    private void checkForSuspiciousPatterns() {
        // Check for brute force attempts (5+ failed logins from same IP in 5 minutes)
        failedLoginByIP.forEach((ip, count) -> {
            if (count.get() > 5) {
                recordSuspiciousActivity(null, ip, "Potential brute force attack", SecuritySeverity.HIGH);
                // Reset counter after recording
                count.set(0);
            }
        });
        
        // Check for multiple failed logins for same user (3+ in 10 minutes)
        failedLoginByUser.forEach((username, count) -> {
            if (count.get() > 3) {
                recordSuspiciousActivity(username, null, "Multiple failed login attempts", SecuritySeverity.MEDIUM);
                // Reset counter after recording
                count.set(0);
            }
        });
    }

    /**
     * Get Recent Suspicious Activities
     */
    private int getRecentSuspiciousActivities() {
        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        
        return (int) recentSecurityEvents.stream()
            .filter(event -> event.getType() == SecurityEventType.SUSPICIOUS_ACTIVITY)
            .filter(event -> event.getTimestamp().isAfter(fiveMinutesAgo))
            .count();
    }

    /**
     * Clean Old Security Events
     */
    private synchronized void cleanOldSecurityEvents() {
        Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));
        
        recentSecurityEvents.removeIf(event -> event.getTimestamp().isBefore(oneHourAgo));
    }

    /**
     * Get Security Summary
     */
    public SecuritySummary getSecuritySummary() {
        return SecuritySummary.builder()
            .failedLoginAttempts(failedLoginAttempts.get())
            .successfulLogins(successfulLogins.get())
            .blockedRequests(blockedRequests.get())
            .suspiciousActivities(suspiciousActivities.get())
            .totalSecurityEvents(totalSecurityEvents.get())
            .loginFailureRate(successfulLogins.get() + failedLoginAttempts.get() > 0 ? 
                (double) failedLoginAttempts.get() / (successfulLogins.get() + failedLoginAttempts.get()) : 0.0)
            .recentSecurityEvents(getRecentSecurityEvents(50))
            .owaspViolations(owaspViolations.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().get())))
            .build();
    }

    /**
     * Get Recent Security Events
     */
    public List<SecurityEvent> getRecentSecurityEvents(int limit) {
        return recentSecurityEvents.stream()
            .sorted((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()))
            .limit(limit)
            .toList();
    }

    /**
     * Security Event Types
     */
    public enum SecurityEventType {
        FAILED_LOGIN, SUCCESSFUL_LOGIN, BLOCKED_REQUEST, SUSPICIOUS_ACTIVITY, OWASP_VIOLATION
    }

    /**
     * Security Severity Levels
     */
    public enum SecuritySeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * OWASP Top 10 Categories
     */
    public enum OWASPCategory {
        A01_BROKEN_ACCESS_CONTROL,
        A02_CRYPTOGRAPHIC_FAILURES,
        A03_INJECTION,
        A04_INSECURE_DESIGN,
        A05_SECURITY_MISCONFIGURATION,
        A06_VULNERABLE_COMPONENTS,
        A07_IDENTIFICATION_FAILURES,
        A08_SOFTWARE_DATA_INTEGRITY,
        A09_LOGGING_MONITORING_FAILURES,
        A10_SSRF
    }

    /**
     * Security Event DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class SecurityEvent {
        private SecurityEventType type;
        private String username;
        private String ipAddress;
        private String endpoint;
        private String description;
        private OWASPCategory owaspCategory;
        private Instant timestamp;
        private SecuritySeverity severity;
    }

    /**
     * Security Summary DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class SecuritySummary {
        private int failedLoginAttempts;
        private int successfulLogins;
        private int blockedRequests;
        private int suspiciousActivities;
        private long totalSecurityEvents;
        private double loginFailureRate;
        private List<SecurityEvent> recentSecurityEvents;
        private Map<OWASPCategory, Integer> owaspViolations;
    }
}