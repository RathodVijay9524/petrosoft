package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for comprehensive audit logging
 * Tracks all business operations, security events, and system activities
 */
@Service
@Slf4j
public class AuditService {
    
    private static final String AUDIT_LOGGER = "AUDIT";
    private static final String SECURITY_LOGGER = "SECURITY";
    private static final String PERFORMANCE_LOGGER = "PERFORMANCE";
    
    /**
     * Log user authentication events
     */
    public void logAuthentication(String username, String event, String status, String details) {
        MDC.put("eventType", "AUTHENTICATION");
        MDC.put("username", username);
        MDC.put("event", event);
        MDC.put("status", status);
        MDC.put("details", details);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        if ("FAILED".equals(status) || "LOCKED".equals(status)) {
            log.warn("Authentication event: {} - {} for user: {} - {}", event, status, username, details);
        } else {
            log.info("Authentication event: {} - {} for user: {}", event, status, username);
        }
        
        MDC.clear();
    }
    
    /**
     * Log user management operations
     */
    public void logUserOperation(String operation, User user, String performedBy, String details) {
        MDC.put("eventType", "USER_MANAGEMENT");
        MDC.put("operation", operation);
        MDC.put("targetUser", user.getUsername());
        MDC.put("performedBy", performedBy);
        MDC.put("details", details);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        log.info("User operation: {} performed by: {} on user: {}", operation, performedBy, user.getUsername());
        MDC.clear();
    }
    
    /**
     * Log business operations
     */
    public void logBusinessOperation(String operation, String entity, Long entityId, String performedBy, Map<String, Object> details) {
        MDC.put("eventType", "BUSINESS_OPERATION");
        MDC.put("operation", operation);
        MDC.put("entity", entity);
        MDC.put("entityId", entityId != null ? entityId.toString() : "null");
        MDC.put("performedBy", performedBy);
        MDC.put("details", details != null ? details.toString() : "null");
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        log.info("Business operation: {} on {} (ID: {}) by {}", operation, entity, entityId, performedBy);
        MDC.clear();
    }
    
    /**
     * Log financial operations
     */
    public void logFinancialOperation(String operation, String entity, Long entityId, String amount, String performedBy, String details) {
        MDC.put("eventType", "FINANCIAL_OPERATION");
        MDC.put("operation", operation);
        MDC.put("entity", entity);
        MDC.put("entityId", entityId != null ? entityId.toString() : "null");
        MDC.put("amount", amount);
        MDC.put("performedBy", performedBy);
        MDC.put("details", details);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        log.info("Financial operation: {} on {} (ID: {}) amount: {} by {}", operation, entity, entityId, amount, performedBy);
        MDC.clear();
    }
    
    /**
     * Log security events
     */
    public void logSecurityEvent(String event, String severity, String details, String userAgent, String ipAddress) {
        MDC.put("eventType", "SECURITY_EVENT");
        MDC.put("event", event);
        MDC.put("severity", severity);
        MDC.put("details", details);
        MDC.put("userAgent", userAgent);
        MDC.put("ipAddress", ipAddress);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        switch (severity.toUpperCase()) {
            case "CRITICAL":
                log.error("SECURITY CRITICAL: {} - {}", event, details);
                break;
            case "HIGH":
                log.error("SECURITY HIGH: {} - {}", event, details);
                break;
            case "MEDIUM":
                log.warn("SECURITY MEDIUM: {} - {}", event, details);
                break;
            case "LOW":
                log.info("SECURITY LOW: {} - {}", event, details);
                break;
            default:
                log.info("SECURITY: {} - {}", event, details);
        }
        
        MDC.clear();
    }
    
    /**
     * Log performance metrics
     */
    public void logPerformance(String operation, long durationMs, String details) {
        MDC.put("eventType", "PERFORMANCE");
        MDC.put("operation", operation);
        MDC.put("durationMs", String.valueOf(durationMs));
        MDC.put("details", details);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        if (durationMs > 5000) {
            log.warn("SLOW OPERATION: {} took {}ms - {}", operation, durationMs, details);
        } else if (durationMs > 1000) {
            log.info("MODERATE OPERATION: {} took {}ms - {}", operation, durationMs, details);
        } else {
            log.info("FAST OPERATION: {} took {}ms - {}", operation, durationMs, details);
        }
        
        MDC.clear();
    }
    
    /**
     * Log system events
     */
    public void logSystemEvent(String event, String level, String details) {
        MDC.put("eventType", "SYSTEM_EVENT");
        MDC.put("event", event);
        MDC.put("level", level);
        MDC.put("details", details);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        switch (level.toUpperCase()) {
            case "ERROR":
                log.error("SYSTEM ERROR: {} - {}", event, details);
                break;
            case "WARN":
                log.warn("SYSTEM WARNING: {} - {}", event, details);
                break;
            case "INFO":
            default:
                log.info("SYSTEM EVENT: {} - {}", event, details);
        }
        
        MDC.clear();
    }
    
    /**
     * Log data access operations
     */
    public void logDataAccess(String operation, String table, Long recordId, String performedBy, String details) {
        MDC.put("eventType", "DATA_ACCESS");
        MDC.put("operation", operation);
        MDC.put("table", table);
        MDC.put("recordId", recordId != null ? recordId.toString() : "null");
        MDC.put("performedBy", performedBy);
        MDC.put("details", details);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        log.info("Data access: {} on {} (ID: {}) by {}", operation, table, recordId, performedBy);
        MDC.clear();
    }
    
    /**
     * Log configuration changes
     */
    public void logConfigurationChange(String configKey, String oldValue, String newValue, String changedBy) {
        MDC.put("eventType", "CONFIGURATION_CHANGE");
        MDC.put("configKey", configKey);
        MDC.put("oldValue", oldValue);
        MDC.put("newValue", newValue);
        MDC.put("changedBy", changedBy);
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        log.info("Configuration changed: {} from '{}' to '{}' by {}", configKey, oldValue, newValue, changedBy);
        MDC.clear();
    }
}
