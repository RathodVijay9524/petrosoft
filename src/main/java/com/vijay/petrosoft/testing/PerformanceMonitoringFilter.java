package com.vijay.petrosoft.testing;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Performance Monitoring Filter
 * Tracks request/response performance and updates metrics
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class PerformanceMonitoringFilter implements Filter {

    private final PerformanceTestConfig performanceConfig;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String endpoint = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        Instant startTime = Instant.now();

        try {
            // Increment active connections
            performanceConfig.incrementActiveConnections();
            
            // Record endpoint request
            performanceConfig.recordEndpointRequest(endpoint);

            // Continue with the request
            chain.doFilter(request, response);

        } catch (Exception e) {
            // Record endpoint error
            performanceConfig.recordEndpointError(endpoint);
            log.error("Error processing request to {}: {}", endpoint, e.getMessage());
            throw e;
        } finally {
            // Decrement active connections
            performanceConfig.decrementActiveConnections();
            
            // Log performance metrics for slow requests
            long duration = java.time.Duration.between(startTime, Instant.now()).toMillis();
            if (duration > 1000) { // Log requests taking more than 1 second
                log.warn("Slow request detected - {} {} took {}ms", method, endpoint, duration);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Performance monitoring filter initialized");
    }

    @Override
    public void destroy() {
        log.info("Performance monitoring filter destroyed");
    }
}
