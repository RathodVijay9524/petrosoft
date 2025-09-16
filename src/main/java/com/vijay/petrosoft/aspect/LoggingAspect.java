package com.vijay.petrosoft.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * AOP Aspect for comprehensive method execution logging
 * Automatically logs method entry, exit, execution time, and parameters
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    /**
     * Pointcut for service methods - OPTIMIZED: Only public methods
     */
    @Pointcut("execution(public * com.vijay.petrosoft.service..*(..))")
    public void serviceMethods() {}
    
    /**
     * Pointcut for controller methods - OPTIMIZED: Only public methods
     */
    @Pointcut("execution(public * com.vijay.petrosoft.controller..*(..))")
    public void controllerMethods() {}
    
    /**
     * Pointcut for repository methods - DISABLED for performance
     */
    // @Pointcut("execution(* com.vijay.petrosoft.repository..*(..))")
    // public void repositoryMethods() {}
    
    /**
     * Around advice for service methods
     */
    @Around("serviceMethods()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "SERVICE");
    }
    
    /**
     * Around advice for controller methods
     */
    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "CONTROLLER");
    }
    
    /**
     * Around advice for repository methods - DISABLED for performance
     */
    // @Around("repositoryMethods()")
    // public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    //     return logMethodExecution(joinPoint, "REPOSITORY");
    // }
    
    /**
     * Common method execution logging logic - OPTIMIZED FOR PERFORMANCE
     */
    private Object logMethodExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String fullMethodName = className + "." + methodName;
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Execute the method
            Object result = joinPoint.proceed();
            
            // Log method exit with execution time - ONLY for slow methods
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (executionTime > 1000) {  // Only log methods taking > 1 second
                log.warn("SLOW {} method: {} completed in {}ms", layer, fullMethodName, executionTime);
            }
            
            return result;
            
        } catch (Exception e) {
            // Log method exception - ONLY errors
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("ERROR in {} method: {} after {}ms - {}", layer, fullMethodName, executionTime, e.getMessage());
            
            throw e;
        }
        // Removed MDC operations for performance
    }
    
    /**
     * Add HTTP request context to MDC if available
     */
    private void addHttpContext() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                MDC.put("requestMethod", request.getMethod());
                MDC.put("requestUri", request.getRequestURI());
                MDC.put("userAgent", request.getHeader("User-Agent"));
                MDC.put("remoteAddr", getClientIpAddress(request));
                
                // Add user context if available
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    MDC.put("hasAuthToken", "true");
                }
            }
        } catch (Exception e) {
            // Ignore errors when getting HTTP context
            log.debug("Could not add HTTP context to MDC: {}", e.getMessage());
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
