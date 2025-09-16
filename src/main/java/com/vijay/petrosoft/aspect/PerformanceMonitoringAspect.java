package com.vijay.petrosoft.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * AOP Aspect for performance monitoring and metrics collection
 * Collects metrics for method execution times, success/failure rates, and business operations
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {
    
    private final MeterRegistry meterRegistry;
    
    public PerformanceMonitoringAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    /**
     * Pointcut for all controller methods
     */
    @Pointcut("execution(* com.vijay.petrosoft.controller..*(..))")
    public void controllerMethods() {}
    
    /**
     * Pointcut for all service methods
     */
    @Pointcut("execution(* com.vijay.petrosoft.service..*(..))")
    public void serviceMethods() {}
    
    /**
     * Pointcut for repository methods
     */
    @Pointcut("execution(* com.vijay.petrosoft.repository..*(..))")
    public void repositoryMethods() {}
    
    /**
     * Around advice for controller methods with performance monitoring
     */
    @Around("controllerMethods()")
    public Object monitorControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodExecution(joinPoint, "controller");
    }
    
    /**
     * Around advice for service methods with performance monitoring
     */
    @Around("serviceMethods()")
    public Object monitorServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodExecution(joinPoint, "service");
    }
    
    /**
     * Around advice for repository methods with performance monitoring
     */
    @Around("repositoryMethods()")
    public Object monitorRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodExecution(joinPoint, "repository");
    }
    
    /**
     * Common method execution monitoring logic
     */
    private Object monitorMethodExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String fullMethodName = className + "." + methodName;
        
        // Create timer for this method
        Timer timer = Timer.builder("method.execution.time")
                .tag("layer", layer)
                .tag("class", className)
                .tag("method", methodName)
                .description("Execution time for " + fullMethodName)
                .register(meterRegistry);
        
        // Create counters for success and failure
        Counter successCounter = Counter.builder("method.execution.count")
                .tag("layer", layer)
                .tag("class", className)
                .tag("method", methodName)
                .tag("status", "success")
                .description("Success count for " + fullMethodName)
                .register(meterRegistry);
        
        Counter failureCounter = Counter.builder("method.execution.count")
                .tag("layer", layer)
                .tag("class", className)
                .tag("method", methodName)
                .tag("status", "failure")
                .description("Failure count for " + fullMethodName)
                .register(meterRegistry);
        
        // Execute method and measure time
        return timer.recordCallable(() -> {
            try {
                Object result = joinPoint.proceed();
                successCounter.increment();
                return result;
            } catch (Throwable throwable) {
                failureCounter.increment();
                throw new RuntimeException(throwable);
            }
        });
    }
}
