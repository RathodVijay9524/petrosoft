package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.SaleTransaction;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleTransactionDTO {

    private Long id;

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    private Long shiftId;
    private Long nozzleId;
    private Long fuelTypeId;
    private Long customerId;

    @NotBlank(message = "Sale number is required")
    private String saleNumber;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.01", message = "Rate must be greater than 0")
    private BigDecimal rate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private BigDecimal discountAmount;
    private BigDecimal taxAmount;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @NotNull(message = "Payment method is required")
    private SaleTransaction.PaymentMethod paymentMethod;

    @NotNull(message = "Sale type is required")
    private SaleTransaction.SaleType saleType;

    private SaleTransaction.Status status;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactedAt;

    private Long operatorId;
    private Long cashierId;
    private String vehicleNumber;
    private String driverName;
    private String notes;
    private String cardLastFour;
    private String cardType;
    private String transactionReference;

    // Additional fields for UI
    private String pumpName;
    private String shiftName;
    private String nozzleName;
    private String fuelTypeName;
    private String customerName;
    private String operatorName;
    private String cashierName;

    // Sale items
    private List<SaleItemDTO> saleItems;

    // Helper methods for UI
    public boolean isCreditSale() {
        return SaleTransaction.PaymentMethod.CREDIT.equals(paymentMethod);
    }

    public boolean isCardSale() {
        return SaleTransaction.PaymentMethod.CARD.equals(paymentMethod);
    }

    public boolean isCashSale() {
        return SaleTransaction.PaymentMethod.CASH.equals(paymentMethod);
    }
}