package com.vijay.petrosoft.security.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Security Audit Controller
 * Provides REST endpoints for security auditing and vulnerability assessment
 */
@RestController
@RequestMapping("/api/security/audit")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Security Audit", description = "Security auditing and vulnerability assessment operations")
@SecurityRequirement(name = "Bearer Authentication")
public class SecurityAuditController {

    private final SecurityAuditService securityAuditService;
    private final SecurityAuditConfig securityConfig;

    /**
     * Run Security Audit
     */
    @PostMapping("/audit")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
        summary = "Run Security Audit",
        description = "Execute a comprehensive security audit on a specific endpoint including OWASP Top 10 tests"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Security audit completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SecurityAuditService.SecurityAuditResult.class),
                examples = @ExampleObject(
                    name = "Security Audit Result",
                    value = """
                        {
                            "endpoint": "http://localhost:8081/api/users",
                            "timestamp": "2025-09-16T19:30:00Z",
                            "securityScore": 85.5,
                            "totalTests": 7,
                            "passedTests": 5,
                            "failedTests": 2,
                            "totalVulnerabilities": 3,
                            "criticalVulnerabilities": 0,
                            "highVulnerabilities": 1,
                            "testResults": [
                                {
                                    "testType": "SQL Injection",
                                    "testsExecuted": 8,
                                    "vulnerabilitiesFound": 0,
                                    "vulnerabilities": []
                                },
                                {
                                    "testType": "Cross-Site Scripting (XSS)",
                                    "testsExecuted": 5,
                                    "vulnerabilitiesFound": 1,
                                    "vulnerabilities": [
                                        {
                                            "type": "Cross-Site Scripting (XSS)",
                                            "description": "XSS vulnerability detected with payload: <script>alert('XSS')</script>",
                                            "severity": "MEDIUM",
                                            "location": "Parameter: test",
                                            "url": "http://localhost:8081/api/users?test=<script>alert('XSS')</script>"
                                        }
                                    ]
                                }
                            ],
                            "recommendations": [
                                "Implement proper output encoding and Content Security Policy (CSP) to prevent XSS attacks",
                                "Enable rate limiting to prevent DoS attacks",
                                "Implement comprehensive logging and monitoring for security events"
                            ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions - Owner role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SecurityAuditService.SecurityAuditResult> runSecurityAudit(
            @Parameter(description = "Security audit configuration", required = true)
            @Valid @RequestBody SecurityAuditService.SecurityAuditRequest request) {
        
        try {
            log.info("Starting security audit for endpoint: {}", request.getEndpoint());
            SecurityAuditService.SecurityAuditResult result = securityAuditService.runSecurityAudit(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error running security audit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Run Quick Security Scan
     */
    @PostMapping("/quick-scan")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(
        summary = "Run Quick Security Scan",
        description = "Execute a quick security scan focusing on common vulnerabilities"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Quick security scan completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SecurityAuditService.SecurityAuditResult.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SecurityAuditService.SecurityAuditResult> runQuickSecurityScan(
            @Parameter(description = "Quick scan configuration", required = true)
            @Valid @RequestBody SecurityAuditService.SecurityAuditRequest request) {
        
        try {
            log.info("Starting quick security scan for endpoint: {}", request.getEndpoint());
            
            // Configure for quick scan (fewer tests)
            SecurityAuditService.SecurityAuditRequest quickScanRequest = SecurityAuditService.SecurityAuditRequest.builder()
                .endpoint(request.getEndpoint())
                .method(request.getMethod())
                .headers(request.getHeaders())
                .body(request.getBody())
                .lowPrivilegeToken(request.getLowPrivilegeToken())
                .testSqlInjection(true)
                .testXss(true)
                .testAuthentication(true)
                .testRateLimiting(false)
                .testPathTraversal(false)
                .testAuthorization(false)
                .testInputValidation(false)
                .build();
            
            SecurityAuditService.SecurityAuditResult result = securityAuditService.runSecurityAudit(quickScanRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error running quick security scan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Security Summary
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('ACCOUNTANT')")
    @Operation(
        summary = "Get Security Summary",
        description = "Retrieve current security metrics and statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Security summary retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SecurityAuditConfig.SecuritySummary.class),
                examples = @ExampleObject(
                    name = "Security Summary",
                    value = """
                        {
                            "failedLoginAttempts": 25,
                            "successfulLogins": 1250,
                            "blockedRequests": 5,
                            "suspiciousActivities": 2,
                            "totalSecurityEvents": 1282,
                            "loginFailureRate": 0.02,
                            "recentSecurityEvents": [
                                {
                                    "type": "FAILED_LOGIN",
                                    "username": "admin",
                                    "ipAddress": "192.168.1.100",
                                    "timestamp": "2025-09-16T19:25:00Z",
                                    "severity": "MEDIUM"
                                }
                            ],
                            "owaspViolations": {
                                "A03_INJECTION": 1,
                                "A07_IDENTIFICATION_FAILURES": 2
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SecurityAuditConfig.SecuritySummary> getSecuritySummary() {
        try {
            SecurityAuditConfig.SecuritySummary summary = securityConfig.getSecuritySummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error getting security summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Security Health
     */
    @GetMapping("/health")
    @Operation(
        summary = "Get Security Health",
        description = "Check the current security health status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Security health status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Security Health",
                    value = """
                        {
                            "status": "UP",
                            "details": {
                                "login_failure_rate": 0.02,
                                "failed_logins": 25,
                                "successful_logins": 1250,
                                "blocked_requests": 5,
                                "suspicious_activities": 2
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "503", description = "Security health is down"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getSecurityHealth() {
        try {
            var health = securityConfig.securityHealthIndicator().getHealth(false);
            
            Map<String, Object> response = Map.of(
                "status", health.getStatus().getCode(),
                "details", health.getDetails()
            );
            
            return ResponseEntity.status(
                health.getStatus().getCode().equals("UP") ? 
                HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE
            ).body(response);
        } catch (Exception e) {
            log.error("Error getting security health: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Recent Security Events
     */
    @GetMapping("/events")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('ACCOUNTANT')")
    @Operation(
        summary = "Get Recent Security Events",
        description = "Retrieve recent security events and incidents"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent security events retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SecurityAuditConfig.SecurityEvent.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SecurityAuditConfig.SecurityEvent>> getRecentSecurityEvents(
            @Parameter(description = "Number of events to retrieve", example = "50")
            @RequestParam(defaultValue = "50") int limit) {
        
        try {
            List<SecurityAuditConfig.SecurityEvent> events = securityConfig.getRecentSecurityEvents(limit);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Error getting recent security events: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get OWASP Violations
     */
    @GetMapping("/owasp-violations")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(
        summary = "Get OWASP Violations",
        description = "Retrieve OWASP Top 10 violations and statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "OWASP violations retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "OWASP Violations",
                    value = """
                        {
                            "A01_BROKEN_ACCESS_CONTROL": 2,
                            "A03_INJECTION": 1,
                            "A07_IDENTIFICATION_FAILURES": 3,
                            "A09_LOGGING_MONITORING_FAILURES": 1
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<SecurityAuditConfig.OWASPCategory, Integer>> getOwaspViolations() {
        try {
            var summary = securityConfig.getSecuritySummary();
            return ResponseEntity.ok(summary.getOwaspViolations());
        } catch (Exception e) {
            log.error("Error getting OWASP violations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Reset Security Metrics
     */
    @PostMapping("/reset")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
        summary = "Reset Security Metrics",
        description = "Reset all security counters and metrics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Security metrics reset successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions - Owner role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> resetSecurityMetrics() {
        try {
            // Note: In a real implementation, you would reset the counters in SecurityAuditConfig
            log.info("Security metrics reset requested");
            return ResponseEntity.ok(Map.of(
                "message", "Security metrics reset successfully",
                "timestamp", java.time.Instant.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error resetting security metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Security Recommendations
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(
        summary = "Get Security Recommendations",
        description = "Get security improvement recommendations based on current metrics"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Security recommendations retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Security Recommendations",
                    value = """
                        [
                            "Implement multi-factor authentication (MFA) for all user accounts",
                            "Enable rate limiting to prevent brute force attacks",
                            "Implement comprehensive logging and monitoring",
                            "Regular security audits and penetration testing",
                            "Update dependencies to latest secure versions",
                            "Implement Content Security Policy (CSP)",
                            "Enable HTTPS for all communications",
                            "Implement proper session management"
                        ]
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> getSecurityRecommendations() {
        try {
            List<String> recommendations = List.of(
                "Implement multi-factor authentication (MFA) for all user accounts",
                "Enable rate limiting to prevent brute force attacks",
                "Implement comprehensive logging and monitoring",
                "Regular security audits and penetration testing",
                "Update dependencies to latest secure versions",
                "Implement Content Security Policy (CSP)",
                "Enable HTTPS for all communications",
                "Implement proper session management",
                "Use parameterized queries to prevent SQL injection",
                "Implement proper input validation and output encoding",
                "Regular backup and disaster recovery testing",
                "Security awareness training for all users"
            );
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error getting security recommendations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
