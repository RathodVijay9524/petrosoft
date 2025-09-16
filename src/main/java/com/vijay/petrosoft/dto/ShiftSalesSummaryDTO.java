package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.ShiftSalesSummary;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftSalesSummaryDTO {

    private Long id;
    private Long shiftId;
    private Long pumpId;
    private Long cashierId;

    private BigDecimal totalSales;
    private BigDecimal cashSales;
    private BigDecimal cardSales;
    private BigDecimal creditSales;
    private BigDecimal upiSales;
    private BigDecimal walletSales;

    private BigDecimal totalQuantity;
    private Integer totalTransactions;
    private Integer cashTransactions;
    private Integer cardTransactions;
    private Integer creditTransactions;

    private BigDecimal openingReading;
    private BigDecimal closingReading;

    @NotNull(message = "Sales date is required")
    private LocalDateTime salesDate;

    @NotNull(message = "Status is required")
    private ShiftSalesSummary.Status status;

    private String notes;
    private Long generatedBy;
    private Long approvedBy;
    private LocalDateTime approvedAt;

    // Additional fields for UI
    private String shiftName;
    private String pumpName;
    private String cashierName;
    private String generatedByName;
    private String approvedByName;

    // Calculated fields
    private BigDecimal totalSalesAmount;
    private BigDecimal totalSalesQuantity;
    private Integer totalSalesTransactions;
}
