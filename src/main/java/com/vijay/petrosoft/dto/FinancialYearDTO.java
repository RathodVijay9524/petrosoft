package com.vijay.petrosoft.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FinancialYearDTO {
    private Long id;
    
    @NotBlank(message = "Financial year name is required")
    @Size(max = 50, message = "Financial year name must not exceed 50 characters")
    private String name;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Builder.Default
    private boolean active = false;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private Long pumpId;
    
    // Computed fields
    private boolean isCurrentYear;
    private boolean isPastYear;
    private boolean isFutureYear;
}
