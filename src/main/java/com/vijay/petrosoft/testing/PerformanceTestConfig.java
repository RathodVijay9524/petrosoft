package com.vijay.petrosoft.testing;

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
 * Performance Testing Configuration
 * Provides comprehensive performance monitoring and testing capabilities
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class PerformanceTestConfig {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final Map<String, AtomicLong> endpointRequestCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> endpointErrorCounts = new ConcurrentHashMap<>();

    /**
     * Performance Health Indicator
     */
    @Bean
    public HealthIndicator performanceHealthIndicator() {
        return () -> {
            try {
                double errorRate = totalRequests.get() > 0 ? 
                    (double) totalErrors.get() / totalRequests.get() : 0.0;
                
                if (errorRate > 0.05) { // 5% error rate threshold
                    return Health.down()
                        .withDetail("error_rate", errorRate)
                        .withDetail("total_requests", totalRequests.get())
                        .withDetail("total_errors", totalErrors.get())
                        .withDetail("active_connections", activeConnections.get())
                        .build();
                } else if (activeConnections.get() > 1000) { // Connection threshold
                    return Health.down()
                        .withDetail("active_connections", activeConnections.get())
                        .withDetail("error_rate", errorRate)
                        .build();
                } else {
                    return Health.up()
                        .withDetail("error_rate", errorRate)
                        .withDetail("total_requests", totalRequests.get())
                        .withDetail("active_connections", activeConnections.get())
                        .build();
                }
            } catch (Exception e) {
                log.error("Error checking performance health", e);
                return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
            }
        };
    }

    /**
     * Register Performance Metrics
     */
    @Bean
    public String registerPerformanceMetrics() {
        log.info("Performance metrics configuration initialized");
        return "performance-metrics-registered";
    }

    /**
     * Scheduled performance monitoring - DISABLED for startup performance
     */
    // @Scheduled(fixedRate = 30000) // DISABLED - Only run when explicitly enabled
    public void monitorPerformance() {
        try {
            double errorRate = totalRequests.get() > 0 ? 
                (double) totalErrors.get() / totalRequests.get() : 0.0;
            
            log.info("Performance Metrics - Active Connections: {}, Total Requests: {}, " +
                    "Total Errors: {}, Error Rate: {:.2f}%",
                activeConnections.get(), totalRequests.get(), 
                totalErrors.get(), errorRate * 100);

            // Log endpoint-specific metrics
            endpointRequestCounts.forEach((endpoint, count) -> {
                long errors = endpointErrorCounts.getOrDefault(endpoint, new AtomicLong(0)).get();
                double endpointErrorRate = count.get() > 0 ? (double) errors / count.get() : 0.0;
                
                if (count.get() > 0) {
                    log.debug("Endpoint {} - Requests: {}, Errors: {}, Error Rate: {:.2f}%",
                        endpoint, count.get(), errors, endpointErrorRate * 100);
                }
            });

        } catch (Exception e) {
            log.error("Error during performance monitoring", e);
        }
    }

    /**
     * Increment active connections
     */
    public void incrementActiveConnections() {
        activeConnections.incrementAndGet();
    }

    /**
     * Decrement active connections
     */
    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }

    /**
     * Increment total requests
     */
    public void incrementTotalRequests() {
        totalRequests.incrementAndGet();
    }

    /**
     * Increment total errors
     */
    public void incrementTotalErrors() {
        totalErrors.incrementAndGet();
    }

    /**
     * Record endpoint request
     */
    public void recordEndpointRequest(String endpoint) {
        endpointRequestCounts.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
        incrementTotalRequests();
    }

    /**
     * Record endpoint error
     */
    public void recordEndpointError(String endpoint) {
        endpointErrorCounts.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
        incrementTotalErrors();
    }

    /**
     * Get performance summary
     */
    public PerformanceSummary getPerformanceSummary() {
        return PerformanceSummary.builder()
            .activeConnections(activeConnections.get())
            .totalRequests(totalRequests.get())
            .totalErrors(totalErrors.get())
            .errorRate(totalRequests.get() > 0 ? 
                (double) totalErrors.get() / totalRequests.get() : 0.0)
            .endpointMetrics(endpointRequestCounts.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        String endpoint = entry.getKey();
                        long requests = entry.getValue().get();
                        long errors = endpointErrorCounts.getOrDefault(endpoint, new AtomicLong(0)).get();
                        return new EndpointMetrics(requests, errors, 
                            requests > 0 ? (double) errors / requests : 0.0);
                    })))
            .build();
    }

    /**
     * Performance Summary DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class PerformanceSummary {
        private int activeConnections;
        private long totalRequests;
        private long totalErrors;
        private double errorRate;
        private Map<String, EndpointMetrics> endpointMetrics;
    }

    /**
     * Endpoint Metrics DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class EndpointMetrics {
        private long requests;
        private long errors;
        private double errorRate;
    }
}