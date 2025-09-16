package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.SaleTransaction;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashierSalesEntryDTO {

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Cashier ID is required")
    private Long cashierId;

    private LocalDateTime entryDate;

    private BigDecimal totalSales;
    private BigDecimal cashSales;
    private BigDecimal cardSales;
    private BigDecimal creditSales;

    private Integer totalTransactions;
    private Integer cashTransactions;
    private Integer cardTransactions;
    private Integer creditTransactions;

    private List<CashierSalesTransactionDTO> transactions;
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CashierSalesTransactionDTO {
        private Long fuelTypeId;
        private Long nozzleId;
        private Long customerId;
        private BigDecimal quantity;
        private BigDecimal rate;
        private BigDecimal amount;
        private SaleTransaction.PaymentMethod paymentMethod;
        private String vehicleNumber;
        private String driverName;
        private String notes;
    }
}
