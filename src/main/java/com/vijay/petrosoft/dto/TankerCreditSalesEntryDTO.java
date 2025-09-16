package com.vijay.petrosoft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TankerCreditSalesEntryDTO {

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Fuel type ID is required")
    private Long fuelTypeId;

    private Long tankId;
    private Long operatorId;
    private Long cashierId;

    @NotNull(message = "Quantity is required")
    private BigDecimal quantity;

    @NotNull(message = "Rate is required")
    private BigDecimal rate;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    private String tankerNumber;
    private String driverName;
    private String driverLicense;
    private String deliveryAddress;
    private String notes;

    // Tanker specific fields
    private String tankerCapacity;
    private BigDecimal density;
    private BigDecimal temperature;
    private String sealNumber;
    private String invoiceNumber;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactedAt;
}
