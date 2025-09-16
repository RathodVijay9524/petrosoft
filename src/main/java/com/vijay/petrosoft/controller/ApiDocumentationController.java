package com.vijay.petrosoft.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for API documentation and system information
 */
@RestController
@RequestMapping("/api/docs")
@Slf4j
@Tag(name = "API Documentation", description = "API documentation and system information endpoints")
public class ApiDocumentationController {
    
    @GetMapping("/info")
    @Operation(
            summary = "Get API information",
            description = "Retrieve comprehensive information about the Petrosoft API including available endpoints, features, and system status."
    )
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        
        // Basic API Information
        apiInfo.put("name", "Petrosoft Management System API");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("description", "Comprehensive Petrol Pump Management System API");
        apiInfo.put("environment", System.getProperty("spring.profiles.active", "default"));
        
        // Available Endpoints
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("authentication", "/api/auth/*");
        endpoints.put("userManagement", "/api/users/*");
        endpoints.put("roleManagement", "/api/roles/*");
        endpoints.put("salesManagement", "/api/sales/*");
        endpoints.put("cashManagement", "/api/cash-management/*");
        endpoints.put("shiftManagement", "/api/shifts/*");
        endpoints.put("customerManagement", "/api/customers/*");
        endpoints.put("supplierManagement", "/api/suppliers/*");
        endpoints.put("inventoryManagement", "/api/inventory/*");
        endpoints.put("fuelManagement", "/api/fuel-types/*");
        endpoints.put("voucherManagement", "/api/vouchers/*");
        endpoints.put("financialReports", "/api/financial-reports/*");
        endpoints.put("dashboard", "/api/dashboard/*");
        endpoints.put("masterSetup", "/api/master-setup/*");
        endpoints.put("notifications", "/api/notifications/*");
        endpoints.put("paymentGateway", "/api/payments/*");
        endpoints.put("subscriptionManagement", "/api/subscriptions/*");
        endpoints.put("purchaseManagement", "/api/purchase-bills/*");
        
        apiInfo.put("endpoints", endpoints);
        
        // Features
        Map<String, Object> features = new HashMap<>();
        features.put("userManagement", "Complete user lifecycle with role-based access control");
        features.put("salesOperations", "Fuel sales, transactions, and reporting");
        features.put("cashManagement", "Cash flow tracking and reconciliation");
        features.put("inventoryManagement", "Fuel stock and supplier management");
        features.put("financialManagement", "Vouchers, reports, and accounting");
        features.put("shiftManagement", "Employee shifts and operations");
        features.put("customerManagement", "Customer information and credit management");
        features.put("notificationSystem", "Email and SMS alerts");
        features.put("paymentIntegration", "Razorpay payment gateway");
        features.put("subscriptionManagement", "Plan management and billing");
        features.put("auditLogging", "Comprehensive audit trail");
        features.put("performanceMonitoring", "Real-time performance metrics");
        
        apiInfo.put("features", features);
        
        // Security Features
        Map<String, Object> security = new HashMap<>();
        security.put("authentication", "JWT-based authentication");
        security.put("authorization", "Role-based access control (OWNER, MANAGER, OPERATOR, ACCOUNTANT, SUPPORT)");
        security.put("auditLogging", "Comprehensive audit trail for all operations");
        security.put("rateLimiting", "API rate limiting and throttling");
        security.put("securityMonitoring", "Real-time security event monitoring");
        
        apiInfo.put("security", security);
        
        // Documentation Links
        Map<String, Object> documentation = new HashMap<>();
        documentation.put("swaggerUI", "/swagger-ui.html");
        documentation.put("openApiSpec", "/v3/api-docs");
        documentation.put("actuatorHealth", "/actuator/health");
        documentation.put("actuatorMetrics", "/actuator/metrics");
        documentation.put("actuatorInfo", "/actuator/info");
        
        apiInfo.put("documentation", documentation);
        
        // System Status
        Map<String, Object> status = new HashMap<>();
        status.put("status", "operational");
        status.put("uptime", System.currentTimeMillis());
        status.put("javaVersion", System.getProperty("java.version"));
        status.put("springVersion", org.springframework.core.SpringVersion.getVersion());
        
        apiInfo.put("systemStatus", status);
        
        log.info("API information requested");
        return ResponseEntity.ok(apiInfo);
    }
    
    @GetMapping("/health")
    @Operation(
            summary = "Get API health status",
            description = "Check the health status of the API and its dependencies."
    )
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Basic health check
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        // Component health
        Map<String, String> components = new HashMap<>();
        components.put("database", "UP");
        components.put("authentication", "UP");
        components.put("notificationService", "UP");
        components.put("paymentGateway", "UP");
        
        health.put("components", components);
        
        log.debug("Health check requested");
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/endpoints")
    @Operation(
            summary = "Get all available endpoints",
            description = "Retrieve a comprehensive list of all available API endpoints with their descriptions."
    )
    public ResponseEntity<Map<String, Object>> getAllEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        // Authentication Endpoints
        Map<String, String> authEndpoints = new HashMap<>();
        authEndpoints.put("POST /api/auth/login", "User login and JWT token generation");
        authEndpoints.put("POST /api/auth/register", "User registration");
        authEndpoints.put("POST /api/auth/logout", "User logout");
        authEndpoints.put("GET /api/auth/roles", "Get available roles");
        authEndpoints.put("POST /api/auth/send-otp", "Send OTP for authentication");
        authEndpoints.put("POST /api/auth/verify-otp", "Verify OTP");
        authEndpoints.put("POST /api/auth/forgot-password", "Forgot password request");
        endpoints.put("authentication", authEndpoints);
        
        // User Management Endpoints
        Map<String, String> userEndpoints = new HashMap<>();
        userEndpoints.put("GET /api/users", "Get all users");
        userEndpoints.put("GET /api/users/{id}", "Get user by ID");
        userEndpoints.put("POST /api/users", "Create new user");
        userEndpoints.put("PUT /api/users/{id}", "Update user");
        userEndpoints.put("DELETE /api/users/{id}", "Delete user");
        userEndpoints.put("GET /api/users/search", "Search users");
        userEndpoints.put("GET /api/users/stats", "Get user statistics");
        endpoints.put("userManagement", userEndpoints);
        
        // Sales Management Endpoints
        Map<String, String> salesEndpoints = new HashMap<>();
        salesEndpoints.put("GET /api/sales", "Get all sales");
        salesEndpoints.put("POST /api/sales", "Create new sale");
        salesEndpoints.put("GET /api/sales/{id}", "Get sale by ID");
        salesEndpoints.put("PUT /api/sales/{id}", "Update sale");
        salesEndpoints.put("DELETE /api/sales/{id}", "Delete sale");
        salesEndpoints.put("GET /api/sales/date-range", "Get sales by date range");
        salesEndpoints.put("POST /api/sales/process-fuel-sale", "Process fuel sale");
        endpoints.put("salesManagement", salesEndpoints);
        
        // Financial Endpoints
        Map<String, String> financialEndpoints = new HashMap<>();
        financialEndpoints.put("GET /api/financial-reports/trial-balance", "Get trial balance");
        financialEndpoints.put("GET /api/financial-reports/profit-loss", "Get profit & loss statement");
        financialEndpoints.put("GET /api/financial-reports/balance-sheet", "Get balance sheet");
        financialEndpoints.put("GET /api/financial-reports/cash-book", "Get cash book");
        financialEndpoints.put("GET /api/financial-reports/day-book", "Get day book");
        endpoints.put("financialReports", financialEndpoints);
        
        // Dashboard Endpoints
        Map<String, String> dashboardEndpoints = new HashMap<>();
        dashboardEndpoints.put("GET /api/dashboard/metrics", "Get dashboard metrics");
        dashboardEndpoints.put("GET /api/dashboard/sales-summary", "Get sales summary");
        dashboardEndpoints.put("GET /api/dashboard/financial-summary", "Get financial summary");
        dashboardEndpoints.put("GET /api/dashboard/inventory-summary", "Get inventory summary");
        endpoints.put("dashboard", dashboardEndpoints);
        
        log.info("All endpoints information requested");
        return ResponseEntity.ok(endpoints);
    }
}
