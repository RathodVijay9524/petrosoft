package com.vijay.petrosoft.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SubscriptionDTO {
    private Long id;
    
    @NotNull(message = "Pump ID is required")
    private Long pumpId;
    
    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name must not exceed 100 characters")
    private String planName;
    
    @NotNull(message = "Start date is required")
    private LocalDate startsAt;
    
    @NotNull(message = "End date is required")
    private LocalDate endsAt;
    
    private boolean active = true;
    
    @DecimalMin(value = "0.0", message = "Amount cannot be negative")
    private Double amount;
    
    @Size(max = 50, message = "Payment status must not exceed 50 characters")
    private String paymentStatus;
    
    private LocalDate lastPaymentDate;
    private LocalDate nextPaymentDate;
    
    @Size(max = 20, message = "Billing cycle must not exceed 20 characters")
    private String billingCycle; // MONTHLY, QUARTERLY, ANNUAL
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
