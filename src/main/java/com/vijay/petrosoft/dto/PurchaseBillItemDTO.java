package com.vijay.petrosoft.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseBillItemDTO {
    
    private Long id;
    
    @NotNull(message = "Fuel type is required")
    private Long fuelTypeId;
    
    private String fuelTypeName;
    
    @NotBlank(message = "Item description is required")
    @Size(max = 500, message = "Item description must not exceed 500 characters")
    private String itemDescription;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    private BigDecimal totalPrice;
    
    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100%")
    private BigDecimal taxRate;
    
    private BigDecimal taxAmount;
    
    @DecimalMin(value = "0.0", message = "Discount rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount rate cannot exceed 100%")
    private BigDecimal discountRate;
    
    private BigDecimal discountAmount;
    
    @NotNull(message = "Net amount is required")
    private BigDecimal netAmount;
    
    private BigDecimal receivedQuantity;
    private BigDecimal remainingQuantity;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    // Additional fields for UI
    private String statusDisplay;
    private boolean fullyReceived;
    private boolean partiallyReceived;
}
