package com.vijay.petrosoft.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "razorpay.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class PaymentGatewayConfig {

    // Razorpay configuration disabled for now
    // Will be enabled when Razorpay dependency is properly configured
    public PaymentGatewayConfig() {
        log.info("PaymentGatewayConfig disabled - Razorpay dependency not available");
    }
}
