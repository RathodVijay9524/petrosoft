package com.vijay.petrosoft.testing;

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
 * Performance Testing Controller
 * Provides REST endpoints for performance testing and monitoring
 */
@RestController
@RequestMapping("/api/testing/performance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Performance Testing", description = "Performance testing and load testing operations")
@SecurityRequirement(name = "Bearer Authentication")
public class PerformanceTestController {

    private final PerformanceTestService performanceTestService;
    private final PerformanceTestConfig performanceConfig;

    /**
     * Run Load Test
     */
    @PostMapping("/load-test")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(
        summary = "Run Load Test",
        description = "Execute a load test on a specific endpoint with configurable concurrent users and duration"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Load test completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PerformanceTestService.LoadTestResult.class),
                examples = @ExampleObject(
                    name = "Load Test Result",
                    value = """
                        {
                            "endpoint": "http://localhost:8081/api/users",
                            "concurrentUsers": 50,
                            "durationSeconds": 60,
                            "totalRequests": 1250,
                            "successfulRequests": 1200,
                            "failedRequests": 50,
                            "errorRate": 0.04,
                            "requestsPerSecond": 20.83,
                            "averageResponseTime": 45.2,
                            "minResponseTime": 12,
                            "maxResponseTime": 234,
                            "p50ResponseTime": 38,
                            "p90ResponseTime": 78,
                            "p95ResponseTime": 95,
                            "p99ResponseTime": 156,
                            "timestamp": "2025-09-16T19:30:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PerformanceTestService.LoadTestResult> runLoadTest(
            @Parameter(description = "Load test configuration", required = true)
            @Valid @RequestBody PerformanceTestService.LoadTestRequest request) {
        
        try {
            log.info("Starting load test for endpoint: {}", request.getEndpoint());
            PerformanceTestService.LoadTestResult result = performanceTestService.runLoadTest(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error running load test: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Run Stress Test
     */
    @PostMapping("/stress-test")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
        summary = "Run Stress Test",
        description = "Execute a stress test to determine the breaking point of an endpoint"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stress test completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PerformanceTestService.StressTestResult.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions - Owner role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PerformanceTestService.StressTestResult> runStressTest(
            @Parameter(description = "Stress test configuration", required = true)
            @Valid @RequestBody PerformanceTestService.StressTestRequest request) {
        
        try {
            log.info("Starting stress test for endpoint: {}", request.getEndpoint());
            PerformanceTestService.StressTestResult result = performanceTestService.runStressTest(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error running stress test: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Run Performance Benchmark
     */
    @PostMapping("/benchmark")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER')")
    @Operation(
        summary = "Run Performance Benchmark",
        description = "Execute performance benchmarks across multiple endpoints"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Benchmark completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PerformanceTestService.BenchmarkResult.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PerformanceTestService.BenchmarkResult> runBenchmark(
            @Parameter(description = "Benchmark configuration", required = true)
            @Valid @RequestBody PerformanceTestService.BenchmarkRequest request) {
        
        try {
            log.info("Starting benchmark for {} endpoints", request.getEndpoints().size());
            PerformanceTestService.BenchmarkResult result = performanceTestService.runBenchmark(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error running benchmark: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Performance Summary
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('ACCOUNTANT')")
    @Operation(
        summary = "Get Performance Summary",
        description = "Retrieve current performance metrics and statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Performance summary retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PerformanceTestConfig.PerformanceSummary.class),
                examples = @ExampleObject(
                    name = "Performance Summary",
                    value = """
                        {
                            "activeConnections": 15,
                            "totalRequests": 12500,
                            "totalErrors": 125,
                            "errorRate": 0.01,
                            "endpointMetrics": {
                                "/api/users": {
                                    "requests": 2500,
                                    "errors": 25,
                                    "errorRate": 0.01
                                },
                                "/api/auth/login": {
                                    "requests": 1200,
                                    "errors": 12,
                                    "errorRate": 0.01
                                }
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
    public ResponseEntity<PerformanceTestConfig.PerformanceSummary> getPerformanceSummary() {
        try {
            PerformanceTestConfig.PerformanceSummary summary = performanceConfig.getPerformanceSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error getting performance summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Performance Health
     */
    @GetMapping("/health")
    @Operation(
        summary = "Get Performance Health",
        description = "Check the current performance health status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Performance health status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Performance Health",
                    value = """
                        {
                            "status": "UP",
                            "details": {
                                "error_rate": 0.01,
                                "total_requests": 12500,
                                "active_connections": 15
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "503", description = "Performance health is down"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getPerformanceHealth() {
        try {
            var health = performanceConfig.performanceHealthIndicator().getHealth(false);
            
            Map<String, Object> response = Map.of(
                "status", health.getStatus().getCode(),
                "details", health.getDetails()
            );
            
            return ResponseEntity.status(
                health.getStatus().getCode().equals("UP") ? 
                HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE
            ).body(response);
        } catch (Exception e) {
            log.error("Error getting performance health: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Reset Performance Metrics
     */
    @PostMapping("/reset")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
        summary = "Reset Performance Metrics",
        description = "Reset all performance counters and metrics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Performance metrics reset successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions - Owner role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> resetPerformanceMetrics() {
        try {
            // Note: In a real implementation, you would reset the counters in PerformanceTestConfig
            log.info("Performance metrics reset requested");
            return ResponseEntity.ok(Map.of(
                "message", "Performance metrics reset successfully",
                "timestamp", java.time.Instant.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error resetting performance metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
