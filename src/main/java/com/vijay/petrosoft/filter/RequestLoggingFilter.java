package com.vijay.petrosoft.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Comprehensive HTTP request/response logging filter
 * Logs all incoming requests and outgoing responses with detailed information
 */
@Component
@Slf4j
public class RequestLoggingFilter implements Filter {
    
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_START_TIME = "requestStartTime";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Generate or extract request ID
        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Set request ID in response header
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Set MDC for structured logging
        setMDC(httpRequest, requestId);
        
        long startTime = System.currentTimeMillis();
        httpRequest.setAttribute(REQUEST_START_TIME, startTime);
        
        try {
            // Log incoming request
            logIncomingRequest(httpRequest, requestId);
            
            // Process request
            chain.doFilter(request, response);
            
        } finally {
            // Log outgoing response
            long duration = System.currentTimeMillis() - startTime;
            logOutgoingResponse(httpRequest, httpResponse, requestId, duration);
            
            // Clear MDC
            MDC.clear();
        }
    }
    
    /**
     * Set MDC values for structured logging
     */
    private void setMDC(HttpServletRequest request, String requestId) {
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        MDC.put("queryString", request.getQueryString());
        MDC.put("userAgent", request.getHeader("User-Agent"));
        MDC.put("remoteAddr", getClientIpAddress(request));
        MDC.put("timestamp", LocalDateTime.now().toString());
        
        // Add authentication context if available
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            MDC.put("hasAuth", "true");
        } else {
            MDC.put("hasAuth", "false");
        }
    }
    
    /**
     * Log incoming HTTP request
     */
    private void logIncomingRequest(HttpServletRequest request, String requestId) {
        StringBuilder requestInfo = new StringBuilder();
        requestInfo.append("INCOMING REQUEST [").append(requestId).append("] ");
        requestInfo.append(request.getMethod()).append(" ");
        requestInfo.append(request.getRequestURI());
        
        if (request.getQueryString() != null) {
            requestInfo.append("?").append(request.getQueryString());
        }
        
        requestInfo.append(" from ").append(getClientIpAddress(request));
        requestInfo.append(" [").append(request.getHeader("User-Agent")).append("]");
        
        // Log at appropriate level based on endpoint
        String uri = request.getRequestURI();
        if (uri.contains("/actuator/") || uri.contains("/swagger") || uri.contains("/api-docs")) {
            log.debug(requestInfo.toString());
        } else {
            log.info(requestInfo.toString());
        }
        
        // Log request headers for debugging
        if (log.isDebugEnabled()) {
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                String headerValue = request.getHeader(headerName);
                // Mask sensitive headers
                if (headerName.toLowerCase().contains("authorization") || 
                    headerName.toLowerCase().contains("password") ||
                    headerName.toLowerCase().contains("token")) {
                    headerValue = "***MASKED***";
                }
                log.debug("Request Header [{}]: {}", headerName, headerValue);
            });
        }
    }
    
    /**
     * Log outgoing HTTP response
     */
    private void logOutgoingResponse(HttpServletRequest request, HttpServletResponse response, 
                                   String requestId, long duration) {
        StringBuilder responseInfo = new StringBuilder();
        responseInfo.append("OUTGOING RESPONSE [").append(requestId).append("] ");
        responseInfo.append(request.getMethod()).append(" ");
        responseInfo.append(request.getRequestURI());
        responseInfo.append(" -> ").append(response.getStatus());
        responseInfo.append(" (").append(duration).append("ms)");
        
        // Add response size if available
        String contentLength = response.getHeader("Content-Length");
        if (contentLength != null) {
            responseInfo.append(" [").append(contentLength).append(" bytes]");
        }
        
        // Log at appropriate level based on status and duration
        int status = response.getStatus();
        String uri = request.getRequestURI();
        
        if (status >= 500) {
            log.error(responseInfo.toString());
        } else if (status >= 400) {
            log.warn(responseInfo.toString());
        } else if (duration > 5000) {
            log.warn("SLOW RESPONSE: " + responseInfo.toString());
        } else if (duration > 1000) {
            log.info("MODERATE RESPONSE: " + responseInfo.toString());
        } else if (uri.contains("/actuator/") || uri.contains("/swagger") || uri.contains("/api-docs")) {
            log.debug(responseInfo.toString());
        } else {
            log.info(responseInfo.toString());
        }
        
        // Log response headers for debugging
        if (log.isDebugEnabled()) {
            response.getHeaderNames().forEach(headerName -> {
                String headerValue = response.getHeader(headerName);
                log.debug("Response Header [{}]: {}", headerName, headerValue);
            });
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
