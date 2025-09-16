package com.vijay.petrosoft.security.audit;

import com.vijay.petrosoft.security.audit.SecurityAuditConfig.SecuritySeverity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Security Audit Service
 * Provides comprehensive security testing and vulnerability assessment
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditService {

    private final SecurityAuditConfig securityConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    // Common attack patterns
    private static final List<String> SQL_INJECTION_PATTERNS = Arrays.asList(
        "' OR '1'='1", "'; DROP TABLE users; --", "1' UNION SELECT * FROM users --",
        "' OR 1=1 --", "admin'--", "' OR 'x'='x", "1' OR '1'='1"
    );
    
    private static final List<String> XSS_PATTERNS = Arrays.asList(
        "<script>alert('XSS')</script>", "<img src=x onerror=alert('XSS')>",
        "javascript:alert('XSS')", "<svg onload=alert('XSS')>",
        "<iframe src=javascript:alert('XSS')></iframe>"
    );
    
    private static final List<String> PATH_TRAVERSAL_PATTERNS = Arrays.asList(
        "../../../etc/passwd", "..\\..\\..\\windows\\system32\\drivers\\etc\\hosts",
        "....//....//....//etc/passwd", "%2e%2e%2f%2e%2e%2f%2e%2e%2fetc%2fpasswd"
    );

    /**
     * Run Comprehensive Security Audit
     */
    public SecurityAuditResult runSecurityAudit(SecurityAuditRequest request) {
        log.info("Starting comprehensive security audit for endpoint: {}", request.getEndpoint());

        List<SecurityTestResult> results = new ArrayList<>();
        List<CompletableFuture<SecurityTestResult>> futures = new ArrayList<>();

        // SQL Injection Tests
        if (request.isTestSqlInjection()) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                testSqlInjection(request), executorService));
        }

        // XSS Tests
        if (request.isTestXss()) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                testXss(request), executorService));
        }

        // Path Traversal Tests
        if (request.isTestPathTraversal()) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                testPathTraversal(request), executorService));
        }

        // Authentication Tests
        if (request.isTestAuthentication()) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                testAuthentication(request), executorService));
        }

        // Authorization Tests
        if (request.isTestAuthorization()) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                testAuthorization(request), executorService));
        }

        // Rate Limiting Tests
        if (request.isTestRateLimiting()) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                testRateLimiting(request), executorService));
        }

        // Input Validation Tests
        if (request.isTestInputValidation()) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                testInputValidation(request), executorService));
        }

        // Wait for all tests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Collect results
        for (CompletableFuture<SecurityTestResult> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                log.error("Error getting security test result", e);
                results.add(SecurityTestResult.builder()
                    .testType("ERROR")
                    .vulnerabilities(List.of(new SecurityVulnerability(
                        "Test Execution Error", e.getMessage(), SecuritySeverity.HIGH)))
                    .build());
            }
        }

        // Calculate overall security score
        double securityScore = calculateSecurityScore(results);

        return SecurityAuditResult.builder()
            .endpoint(request.getEndpoint())
            .timestamp(Instant.now())
            .securityScore(securityScore)
            .totalTests(results.size())
            .passedTests((int) results.stream().filter(r -> r.getVulnerabilities().isEmpty()).count())
            .failedTests((int) results.stream().filter(r -> !r.getVulnerabilities().isEmpty()).count())
            .totalVulnerabilities(results.stream().mapToInt(r -> r.getVulnerabilities().size()).sum())
            .criticalVulnerabilities(results.stream()
                .flatMap(r -> r.getVulnerabilities().stream())
                .filter(v -> v.getSeverity() == SecuritySeverity.CRITICAL)
                .count())
            .highVulnerabilities(results.stream()
                .flatMap(r -> r.getVulnerabilities().stream())
                .filter(v -> v.getSeverity() == SecuritySeverity.HIGH)
                .count())
            .testResults(results)
            .recommendations(generateRecommendations(results))
            .build();
    }

    /**
     * Test SQL Injection Vulnerabilities
     */
    private SecurityTestResult testSqlInjection(SecurityAuditRequest request) {
        log.debug("Testing SQL injection for endpoint: {}", request.getEndpoint());
        
        List<SecurityVulnerability> vulnerabilities = new ArrayList<>();
        int testsExecuted = 0;
        int vulnerabilitiesFound = 0;

        for (String payload : SQL_INJECTION_PATTERNS) {
            try {
                testsExecuted++;
                
                // Test in URL parameters
                String testUrl = request.getEndpoint() + "?test=" + payload;
                ResponseEntity<String> response = executeRequest(testUrl, request.getMethod(), request.getHeaders());
                
                if (isSqlInjectionVulnerable(response)) {
                    vulnerabilitiesFound++;
                    vulnerabilities.add(new SecurityVulnerability(
                        "SQL Injection",
                        "SQL injection vulnerability detected with payload: " + payload,
                        SecuritySeverity.HIGH,
                        "Parameter: test",
                        testUrl
                    ));
                }

                // Test in request body (for POST/PUT requests)
                if (request.getMethod() == HttpMethod.POST || request.getMethod() == HttpMethod.PUT) {
                    String testBody = request.getBody() != null ? 
                        request.getBody().replace("{{payload}}", payload) : 
                        "{\"test\": \"" + payload + "\"}";
                    
                    response = executeRequest(request.getEndpoint(), request.getMethod(), 
                        request.getHeaders(), testBody);
                    
                    if (isSqlInjectionVulnerable(response)) {
                        vulnerabilitiesFound++;
                        vulnerabilities.add(new SecurityVulnerability(
                            "SQL Injection",
                            "SQL injection vulnerability detected in request body with payload: " + payload,
                            SecuritySeverity.HIGH,
                            "Request Body",
                            request.getEndpoint()
                        ));
                    }
                }

            } catch (Exception e) {
                log.debug("SQL injection test failed for payload: {} - {}", payload, e.getMessage());
            }
        }

        return SecurityTestResult.builder()
            .testType("SQL Injection")
            .testsExecuted(testsExecuted)
            .vulnerabilitiesFound(vulnerabilitiesFound)
            .vulnerabilities(vulnerabilities)
            .build();
    }

    /**
     * Test XSS Vulnerabilities
     */
    private SecurityTestResult testXss(SecurityAuditRequest request) {
        log.debug("Testing XSS for endpoint: {}", request.getEndpoint());
        
        List<SecurityVulnerability> vulnerabilities = new ArrayList<>();
        int testsExecuted = 0;
        int vulnerabilitiesFound = 0;

        for (String payload : XSS_PATTERNS) {
            try {
                testsExecuted++;
                
                // Test in URL parameters
                String testUrl = request.getEndpoint() + "?test=" + payload;
                ResponseEntity<String> response = executeRequest(testUrl, request.getMethod(), request.getHeaders());
                
                if (isXssVulnerable(response, payload)) {
                    vulnerabilitiesFound++;
                    vulnerabilities.add(new SecurityVulnerability(
                        "Cross-Site Scripting (XSS)",
                        "XSS vulnerability detected with payload: " + payload,
                        SecuritySeverity.MEDIUM,
                        "Parameter: test",
                        testUrl
                    ));
                }

            } catch (Exception e) {
                log.debug("XSS test failed for payload: {} - {}", payload, e.getMessage());
            }
        }

        return SecurityTestResult.builder()
            .testType("Cross-Site Scripting (XSS)")
            .testsExecuted(testsExecuted)
            .vulnerabilitiesFound(vulnerabilitiesFound)
            .vulnerabilities(vulnerabilities)
            .build();
    }

    /**
     * Test Path Traversal Vulnerabilities
     */
    private SecurityTestResult testPathTraversal(SecurityAuditRequest request) {
        log.debug("Testing path traversal for endpoint: {}", request.getEndpoint());
        
        List<SecurityVulnerability> vulnerabilities = new ArrayList<>();
        int testsExecuted = 0;
        int vulnerabilitiesFound = 0;

        for (String payload : PATH_TRAVERSAL_PATTERNS) {
            try {
                testsExecuted++;
                
                // Test in URL path
                String testUrl = request.getEndpoint() + "/" + payload;
                ResponseEntity<String> response = executeRequest(testUrl, request.getMethod(), request.getHeaders());
                
                if (isPathTraversalVulnerable(response)) {
                    vulnerabilitiesFound++;
                    vulnerabilities.add(new SecurityVulnerability(
                        "Path Traversal",
                        "Path traversal vulnerability detected with payload: " + payload,
                        SecuritySeverity.HIGH,
                        "URL Path",
                        testUrl
                    ));
                }

            } catch (Exception e) {
                log.debug("Path traversal test failed for payload: {} - {}", payload, e.getMessage());
            }
        }

        return SecurityTestResult.builder()
            .testType("Path Traversal")
            .testsExecuted(testsExecuted)
            .vulnerabilitiesFound(vulnerabilitiesFound)
            .vulnerabilities(vulnerabilities)
            .build();
    }

    /**
     * Test Authentication
     */
    private SecurityTestResult testAuthentication(SecurityAuditRequest request) {
        log.debug("Testing authentication for endpoint: {}", request.getEndpoint());
        
        List<SecurityVulnerability> vulnerabilities = new ArrayList<>();
        int testsExecuted = 0;
        int vulnerabilitiesFound = 0;

        try {
            // Test without authentication
            testsExecuted++;
            ResponseEntity<String> response = executeRequest(request.getEndpoint(), request.getMethod(), new HttpHeaders());
            
            if (response.getStatusCode().is2xxSuccessful()) {
                vulnerabilitiesFound++;
                vulnerabilities.add(new SecurityVulnerability(
                    "Authentication Bypass",
                    "Endpoint accessible without authentication",
                    SecuritySeverity.HIGH,
                    "Authentication",
                    request.getEndpoint()
                ));
            }

            // Test with invalid token
            testsExecuted++;
            HttpHeaders invalidHeaders = new HttpHeaders();
            invalidHeaders.set("Authorization", "Bearer invalid_token");
            
            response = executeRequest(request.getEndpoint(), request.getMethod(), invalidHeaders);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                vulnerabilitiesFound++;
                vulnerabilities.add(new SecurityVulnerability(
                    "Authentication Bypass",
                    "Endpoint accessible with invalid authentication token",
                    SecuritySeverity.HIGH,
                    "Authentication",
                    request.getEndpoint()
                ));
            }

        } catch (Exception e) {
            log.debug("Authentication test failed: {}", e.getMessage());
        }

        return SecurityTestResult.builder()
            .testType("Authentication")
            .testsExecuted(testsExecuted)
            .vulnerabilitiesFound(vulnerabilitiesFound)
            .vulnerabilities(vulnerabilities)
            .build();
    }

    /**
     * Test Authorization
     */
    private SecurityTestResult testAuthorization(SecurityAuditRequest request) {
        log.debug("Testing authorization for endpoint: {}", request.getEndpoint());
        
        List<SecurityVulnerability> vulnerabilities = new ArrayList<>();
        int testsExecuted = 0;
        int vulnerabilitiesFound = 0;

        try {
            // Test with low-privilege token (if provided)
            if (request.getLowPrivilegeToken() != null) {
                testsExecuted++;
                HttpHeaders lowPrivHeaders = new HttpHeaders();
                lowPrivHeaders.set("Authorization", "Bearer " + request.getLowPrivilegeToken());
                
                ResponseEntity<String> response = executeRequest(request.getEndpoint(), request.getMethod(), lowPrivHeaders);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    vulnerabilitiesFound++;
                    vulnerabilities.add(new SecurityVulnerability(
                        "Authorization Bypass",
                        "Endpoint accessible with insufficient privileges",
                        SecuritySeverity.HIGH,
                        "Authorization",
                        request.getEndpoint()
                    ));
                }
            }

        } catch (Exception e) {
            log.debug("Authorization test failed: {}", e.getMessage());
        }

        return SecurityTestResult.builder()
            .testType("Authorization")
            .testsExecuted(testsExecuted)
            .vulnerabilitiesFound(vulnerabilitiesFound)
            .vulnerabilities(vulnerabilities)
            .build();
    }

    /**
     * Test Rate Limiting
     */
    private SecurityTestResult testRateLimiting(SecurityAuditRequest request) {
        log.debug("Testing rate limiting for endpoint: {}", request.getEndpoint());
        
        List<SecurityVulnerability> vulnerabilities = new ArrayList<>();
        int testsExecuted = 0;
        int vulnerabilitiesFound = 0;

        try {
            // Send rapid requests to test rate limiting
            for (int i = 0; i < 100; i++) {
                testsExecuted++;
                ResponseEntity<String> response = executeRequest(request.getEndpoint(), request.getMethod(), request.getHeaders());
                
                if (i > 50 && response.getStatusCode().is2xxSuccessful()) {
                    // If we can send more than 50 requests without being rate limited
                    vulnerabilitiesFound++;
                    vulnerabilities.add(new SecurityVulnerability(
                        "Rate Limiting Bypass",
                        "No rate limiting detected - endpoint vulnerable to DoS attacks",
                        SecuritySeverity.MEDIUM,
                        "Rate Limiting",
                        request.getEndpoint()
                    ));
                    break;
                }
                
                Thread.sleep(10); // Small delay between requests
            }

        } catch (Exception e) {
            log.debug("Rate limiting test failed: {}", e.getMessage());
        }

        return SecurityTestResult.builder()
            .testType("Rate Limiting")
            .testsExecuted(testsExecuted)
            .vulnerabilitiesFound(vulnerabilitiesFound)
            .vulnerabilities(vulnerabilities)
            .build();
    }

    /**
     * Test Input Validation
     */
    private SecurityTestResult testInputValidation(SecurityAuditRequest request) {
        log.debug("Testing input validation for endpoint: {}", request.getEndpoint());
        
        List<SecurityVulnerability> vulnerabilities = new ArrayList<>();
        int testsExecuted = 0;
        int vulnerabilitiesFound = 0;

        // Test with various malicious inputs
        String[] maliciousInputs = {
            "<script>alert('test')</script>",
            "../../../etc/passwd",
            "'; DROP TABLE users; --",
            "admin' OR '1'='1",
            "null",
            "",
            " ",
            "a".repeat(10000) // Very long string
        };

        for (String input : maliciousInputs) {
            try {
                testsExecuted++;
                
                String testUrl = request.getEndpoint() + "?input=" + input;
                ResponseEntity<String> response = executeRequest(testUrl, request.getMethod(), request.getHeaders());
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    vulnerabilitiesFound++;
                    vulnerabilities.add(new SecurityVulnerability(
                        "Input Validation",
                        "Insufficient input validation for malicious input: " + input,
                        SecuritySeverity.MEDIUM,
                        "Input Validation",
                        testUrl
                    ));
                }

            } catch (Exception e) {
                log.debug("Input validation test failed for input: {} - {}", input, e.getMessage());
            }
        }

        return SecurityTestResult.builder()
            .testType("Input Validation")
            .testsExecuted(testsExecuted)
            .vulnerabilitiesFound(vulnerabilitiesFound)
            .vulnerabilities(vulnerabilities)
            .build();
    }

    /**
     * Execute HTTP Request
     */
    private ResponseEntity<String> executeRequest(String url, HttpMethod method, HttpHeaders headers) {
        return executeRequest(url, method, headers, null);
    }

    private ResponseEntity<String> executeRequest(String url, HttpMethod method, HttpHeaders headers, String body) {
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        
        try {
            return restTemplate.exchange(url, method, entity, String.class);
        } catch (Exception e) {
            // Return a mock response for testing purposes
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    /**
     * Check if response indicates SQL injection vulnerability
     */
    private boolean isSqlInjectionVulnerable(ResponseEntity<String> response) {
        if (response.getBody() == null) return false;
        
        String body = response.getBody().toLowerCase();
        return body.contains("sql") || body.contains("mysql") || body.contains("database") ||
               body.contains("error") || body.contains("exception") || body.contains("syntax");
    }

    /**
     * Check if response indicates XSS vulnerability
     */
    private boolean isXssVulnerable(ResponseEntity<String> response, String payload) {
        if (response.getBody() == null) return false;
        
        String body = response.getBody();
        return body.contains(payload) && !body.contains("&lt;") && !body.contains("&gt;");
    }

    /**
     * Check if response indicates path traversal vulnerability
     */
    private boolean isPathTraversalVulnerable(ResponseEntity<String> response) {
        if (response.getBody() == null) return false;
        
        String body = response.getBody().toLowerCase();
        return body.contains("root:") || body.contains("passwd") || body.contains("hosts") ||
               body.contains("system32") || body.contains("windows");
    }

    /**
     * Calculate Security Score
     */
    private double calculateSecurityScore(List<SecurityTestResult> results) {
        if (results.isEmpty()) return 100.0;
        
        int totalVulnerabilities = results.stream()
            .mapToInt(r -> r.getVulnerabilities().size())
            .sum();
        
        int criticalVulns = results.stream()
            .flatMap(r -> r.getVulnerabilities().stream())
            .mapToInt(v -> v.getSeverity() == SecuritySeverity.CRITICAL ? 1 : 0)
            .sum();
        
        int highVulns = results.stream()
            .flatMap(r -> r.getVulnerabilities().stream())
            .mapToInt(v -> v.getSeverity() == SecuritySeverity.HIGH ? 1 : 0)
            .sum();
        
        int mediumVulns = results.stream()
            .flatMap(r -> r.getVulnerabilities().stream())
            .mapToInt(v -> v.getSeverity() == SecuritySeverity.MEDIUM ? 1 : 0)
            .sum();
        
        // Calculate score (critical = -20, high = -10, medium = -5, low = -2)
        double score = 100.0 - (criticalVulns * 20.0) - (highVulns * 10.0) - (mediumVulns * 5.0);
        
        return Math.max(0.0, score);
    }

    /**
     * Generate Security Recommendations
     */
    private List<String> generateRecommendations(List<SecurityTestResult> results) {
        List<String> recommendations = new ArrayList<>();
        
        boolean hasSqlInjection = results.stream()
            .anyMatch(r -> r.getTestType().equals("SQL Injection") && !r.getVulnerabilities().isEmpty());
        
        boolean hasXss = results.stream()
            .anyMatch(r -> r.getTestType().equals("Cross-Site Scripting (XSS)") && !r.getVulnerabilities().isEmpty());
        
        boolean hasAuthIssues = results.stream()
            .anyMatch(r -> r.getTestType().equals("Authentication") && !r.getVulnerabilities().isEmpty());
        
        if (hasSqlInjection) {
            recommendations.add("Implement parameterized queries and input validation to prevent SQL injection attacks");
        }
        
        if (hasXss) {
            recommendations.add("Implement proper output encoding and Content Security Policy (CSP) to prevent XSS attacks");
        }
        
        if (hasAuthIssues) {
            recommendations.add("Implement proper authentication and authorization mechanisms");
        }
        
        recommendations.add("Enable rate limiting to prevent DoS attacks");
        recommendations.add("Implement comprehensive logging and monitoring for security events");
        recommendations.add("Regular security testing and penetration testing");
        
        return recommendations;
    }

    /**
     * Security Audit Request DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class SecurityAuditRequest {
        private String endpoint;
        private HttpMethod method = HttpMethod.GET;
        private HttpHeaders headers;
        private String body;
        private String lowPrivilegeToken;
        private boolean testSqlInjection = true;
        private boolean testXss = true;
        private boolean testPathTraversal = true;
        private boolean testAuthentication = true;
        private boolean testAuthorization = true;
        private boolean testRateLimiting = true;
        private boolean testInputValidation = true;
    }

    /**
     * Security Test Result DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class SecurityTestResult {
        private String testType;
        private int testsExecuted;
        private int vulnerabilitiesFound;
        private List<SecurityVulnerability> vulnerabilities;
    }

    /**
     * Security Vulnerability DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SecurityVulnerability {
        private String type;
        private String description;
        private SecuritySeverity severity;
        private String location;
        private String url;
        
        public SecurityVulnerability(String type, String description, SecuritySeverity severity) {
            this(type, description, severity, null, null);
        }
    }

    /**
     * Security Audit Result DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class SecurityAuditResult {
        private String endpoint;
        private Instant timestamp;
        private double securityScore;
        private int totalTests;
        private int passedTests;
        private int failedTests;
        private int totalVulnerabilities;
        private long criticalVulnerabilities;
        private long highVulnerabilities;
        private List<SecurityTestResult> testResults;
        private List<String> recommendations;
    }
}
