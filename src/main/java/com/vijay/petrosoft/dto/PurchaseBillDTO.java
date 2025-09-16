package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.PurchaseBill;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseBillDTO {
    
    private Long id;
    
    @NotBlank(message = "Bill number is required")
    @Size(max = 50, message = "Bill number must not exceed 50 characters")
    private String billNumber;
    
    @NotNull(message = "Bill date is required")
    private LocalDate billDate;
    
    @NotNull(message = "Supplier is required")
    private Long supplierId;
    
    private String supplierName;
    private String supplierCode;
    
    @NotNull(message = "Pump ID is required")
    private Long pumpId;
    
    @NotNull(message = "Bill type is required")
    private PurchaseBill.BillType billType;
    
    private PurchaseBill.Status status;
    
    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;
    
    private String paymentTerms;
    private LocalDate dueDate;
    
    private PurchaseBill.PaymentStatus paymentStatus;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @Size(max = 100, message = "Reference number must not exceed 100 characters")
    private String referenceNumber;
    
    @Size(max = 20, message = "Vehicle number must not exceed 20 characters")
    private String vehicleNumber;
    
    @Size(max = 100, message = "Driver name must not exceed 100 characters")
    private String driverName;
    
    private Long approvedBy;
    private LocalDateTime approvedAt;
    
    @Valid
    @NotEmpty(message = "At least one item is required")
    private List<PurchaseBillItemDTO> items;
    
    // Additional fields for UI
    private String statusDisplay;
    private String paymentStatusDisplay;
    private String billTypeDisplay;
    private String supplierDisplay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
