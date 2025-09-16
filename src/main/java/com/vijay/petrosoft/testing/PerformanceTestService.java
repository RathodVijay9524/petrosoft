package com.vijay.petrosoft.testing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance Testing Service
 * Provides comprehensive load testing and performance analysis capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceTestService {

    private final PerformanceTestConfig performanceConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);

    /**
     * Run Load Test on Endpoint
     */
    public LoadTestResult runLoadTest(LoadTestRequest request) {
        log.info("Starting load test for endpoint: {} with {} concurrent users for {} seconds", 
            request.getEndpoint(), request.getConcurrentUsers(), request.getDurationSeconds());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        AtomicInteger minResponseTime = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxResponseTime = new AtomicInteger(0);
        
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(request.getConcurrentUsers());

        // Start load test
        for (int i = 0; i < request.getConcurrentUsers(); i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    
                    Instant endTime = Instant.now().plusSeconds(request.getDurationSeconds());
                    
                    while (Instant.now().isBefore(endTime)) {
                        try {
                            long startTime = System.currentTimeMillis();
                            
                            ResponseEntity<String> response = executeRequest(request);
                            
                            long responseTime = System.currentTimeMillis() - startTime;
                            responseTimes.add(responseTime);
                            totalResponseTime.addAndGet(responseTime);
                            
                            if (response.getStatusCode().is2xxSuccessful()) {
                                successCount.incrementAndGet();
                            } else {
                                errorCount.incrementAndGet();
                                errors.add("HTTP " + response.getStatusCode().value() + ": " + response.getBody());
                            }
                            
                            // Update min/max response times
                            updateMinMaxResponseTime(responseTime, minResponseTime, maxResponseTime);
                            
                            // Small delay to prevent overwhelming the system
                            Thread.sleep(100);
                            
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                            errors.add(e.getMessage());
                            log.debug("Request failed: {}", e.getMessage());
                        }
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Load test thread interrupted");
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Start the test
        startLatch.countDown();
        
        try {
            finishLatch.await(request.getDurationSeconds() + 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Load test interrupted");
        }

        // Calculate statistics
        int totalRequests = successCount.get() + errorCount.get();
        double avgResponseTime = totalRequests > 0 ? (double) totalResponseTime.get() / totalRequests : 0;
        double requestsPerSecond = totalRequests / (double) request.getDurationSeconds();
        double errorRate = totalRequests > 0 ? (double) errorCount.get() / totalRequests : 0;
        
        // Calculate percentiles
        Collections.sort(responseTimes);
        int p50 = calculatePercentile(responseTimes, 50);
        int p90 = calculatePercentile(responseTimes, 90);
        int p95 = calculatePercentile(responseTimes, 95);
        int p99 = calculatePercentile(responseTimes, 99);

        LoadTestResult result = LoadTestResult.builder()
            .endpoint(request.getEndpoint())
            .concurrentUsers(request.getConcurrentUsers())
            .durationSeconds(request.getDurationSeconds())
            .totalRequests(totalRequests)
            .successfulRequests(successCount.get())
            .failedRequests(errorCount.get())
            .errorRate(errorRate)
            .requestsPerSecond(requestsPerSecond)
            .averageResponseTime(avgResponseTime)
            .minResponseTime(minResponseTime.get() == Integer.MAX_VALUE ? 0 : minResponseTime.get())
            .maxResponseTime(maxResponseTime.get())
            .p50ResponseTime(p50)
            .p90ResponseTime(p90)
            .p95ResponseTime(p95)
            .p99ResponseTime(p99)
            .errors(errors)
            .timestamp(Instant.now())
            .build();

        log.info("Load test completed for {}: {} requests, {} RPS, {:.2f}ms avg response time, {:.2f}% error rate",
            request.getEndpoint(), totalRequests, requestsPerSecond, avgResponseTime, errorRate * 100);

        return result;
    }

    /**
     * Run Stress Test
     */
    public StressTestResult runStressTest(StressTestRequest request) {
        log.info("Starting stress test for endpoint: {}", request.getEndpoint());

        List<LoadTestResult> results = new ArrayList<>();
        int currentUsers = request.getInitialUsers();
        
        while (currentUsers <= request.getMaxUsers()) {
            log.info("Running stress test with {} concurrent users", currentUsers);
            
            LoadTestRequest loadTestRequest = LoadTestRequest.builder()
                .endpoint(request.getEndpoint())
                .method(request.getMethod())
                .headers(request.getHeaders())
                .body(request.getBody())
                .concurrentUsers(currentUsers)
                .durationSeconds(request.getTestDurationSeconds())
                .build();
                
            LoadTestResult result = runLoadTest(loadTestRequest);
            results.add(result);
            
            // Check if we should continue based on error rate
            if (result.getErrorRate() > request.getMaxErrorRate()) {
                log.warn("Error rate {} exceeds maximum {} at {} users, stopping stress test",
                    result.getErrorRate(), request.getMaxErrorRate(), currentUsers);
                break;
            }
            
            currentUsers += request.getUserIncrement();
            
            // Wait between tests
            try {
                Thread.sleep(request.getWaitBetweenTestsSeconds() * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return StressTestResult.builder()
            .endpoint(request.getEndpoint())
            .results(results)
            .maxUsersTested(currentUsers - request.getUserIncrement())
            .timestamp(Instant.now())
            .build();
    }

    /**
     * Run Performance Benchmark
     */
    public BenchmarkResult runBenchmark(BenchmarkRequest request) {
        log.info("Running performance benchmark for {} endpoints", request.getEndpoints().size());

        Map<String, LoadTestResult> results = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String endpoint : request.getEndpoints()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                LoadTestRequest loadTestRequest = LoadTestRequest.builder()
                    .endpoint(endpoint)
                    .method(request.getMethod())
                    .headers(request.getHeaders())
                    .body(request.getBody())
                    .concurrentUsers(request.getConcurrentUsers())
                    .durationSeconds(request.getDurationSeconds())
                    .build();
                    
                LoadTestResult result = runLoadTest(loadTestRequest);
                results.put(endpoint, result);
            }, executorService);
            
            futures.add(future);
        }

        // Wait for all tests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return BenchmarkResult.builder()
            .endpoints(request.getEndpoints())
            .results(results)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * Execute HTTP Request
     */
    private ResponseEntity<String> executeRequest(LoadTestRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if (request.getHeaders() != null) {
            headers.putAll(request.getHeaders());
        }
        
        HttpEntity<String> entity = new HttpEntity<>(request.getBody(), headers);
        
        return restTemplate.exchange(
            request.getEndpoint(),
            request.getMethod(),
            entity,
            String.class
        );
    }

    /**
     * Update Min/Max Response Time
     */
    private void updateMinMaxResponseTime(long responseTime, AtomicInteger min, AtomicInteger max) {
        int time = (int) responseTime;
        
        int currentMin;
        do {
            currentMin = min.get();
            if (time >= currentMin) break;
        } while (!min.compareAndSet(currentMin, time));
        
        int currentMax;
        do {
            currentMax = max.get();
            if (time <= currentMax) break;
        } while (!max.compareAndSet(currentMax, time));
    }

    /**
     * Calculate Percentile
     */
    private int calculatePercentile(List<Long> values, int percentile) {
        if (values.isEmpty()) return 0;
        
        int index = (int) Math.ceil((percentile / 100.0) * values.size()) - 1;
        index = Math.max(0, Math.min(index, values.size() - 1));
        
        return values.get(index).intValue();
    }

    /**
     * Load Test Request DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class LoadTestRequest {
        private String endpoint;
        private HttpMethod method = HttpMethod.GET;
        private Map<String, List<String>> headers;
        private String body;
        private int concurrentUsers = 10;
        private int durationSeconds = 60;
    }

    /**
     * Load Test Result DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class LoadTestResult {
        private String endpoint;
        private int concurrentUsers;
        private int durationSeconds;
        private int totalRequests;
        private int successfulRequests;
        private int failedRequests;
        private double errorRate;
        private double requestsPerSecond;
        private double averageResponseTime;
        private int minResponseTime;
        private int maxResponseTime;
        private int p50ResponseTime;
        private int p90ResponseTime;
        private int p95ResponseTime;
        private int p99ResponseTime;
        private List<String> errors;
        private Instant timestamp;
    }

    /**
     * Stress Test Request DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class StressTestRequest {
        private String endpoint;
        private HttpMethod method = HttpMethod.GET;
        private Map<String, List<String>> headers;
        private String body;
        private int initialUsers = 10;
        private int maxUsers = 100;
        private int userIncrement = 10;
        private int testDurationSeconds = 30;
        private int waitBetweenTestsSeconds = 5;
        private double maxErrorRate = 0.05; // 5%
    }

    /**
     * Stress Test Result DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class StressTestResult {
        private String endpoint;
        private List<LoadTestResult> results;
        private int maxUsersTested;
        private Instant timestamp;
    }

    /**
     * Benchmark Request DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class BenchmarkRequest {
        private List<String> endpoints;
        private HttpMethod method = HttpMethod.GET;
        private Map<String, List<String>> headers;
        private String body;
        private int concurrentUsers = 10;
        private int durationSeconds = 30;
    }

    /**
     * Benchmark Result DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class BenchmarkResult {
        private List<String> endpoints;
        private Map<String, LoadTestResult> results;
        private Instant timestamp;
    }
}
