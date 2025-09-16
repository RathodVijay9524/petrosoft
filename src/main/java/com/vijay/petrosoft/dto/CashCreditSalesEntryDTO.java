package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.SaleTransaction;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashCreditSalesEntryDTO {

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Payment method is required")
    private SaleTransaction.PaymentMethod paymentMethod;

    @NotNull(message = "Fuel type ID is required")
    private Long fuelTypeId;

    private Long nozzleId;
    private Long customerId;
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

    private String vehicleNumber;
    private String driverName;
    private String notes;

    // For credit sales
    private String customerCode;
    private BigDecimal creditLimit;
    private BigDecimal outstandingBalance;

    // For card sales
    private String cardLastFour;
    private String cardType;
    private String transactionReference;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactedAt;
}
