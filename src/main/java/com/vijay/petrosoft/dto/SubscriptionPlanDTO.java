package com.vijay.petrosoft.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SubscriptionPlanDTO {
    private Long id;
    
    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private Double price;
    
    @NotBlank(message = "Billing cycle is required")
    @Pattern(regexp = "MONTHLY|QUARTERLY|ANNUAL", message = "Billing cycle must be MONTHLY, QUARTERLY, or ANNUAL")
    private String billingCycle;
    
    private boolean active = true;
    
    private List<String> features;
    
    @Min(value = 1, message = "Max pumps must be at least 1")
    private Integer maxPumps;
    
    @Min(value = 1, message = "Max users must be at least 1")
    private Integer maxUsers;
    
    private boolean supportIncluded;
    private boolean reportingIncluded;
    private boolean apiAccess;
}
