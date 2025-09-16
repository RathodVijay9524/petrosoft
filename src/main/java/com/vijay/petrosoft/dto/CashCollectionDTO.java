package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.CashCollection;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashCollectionDTO {

    private Long id;

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Cashier ID is required")
    private Long cashierId;

    @NotNull(message = "Collection date is required")
    private LocalDateTime collectionDate;

    private BigDecimal openingCash;
    private BigDecimal closingCash;
    private BigDecimal totalSales;
    private BigDecimal cashSales;
    private BigDecimal cardSales;
    private BigDecimal creditSales;
    private BigDecimal cashCollected;
    private BigDecimal cashShortage;
    private BigDecimal cashExcess;
    private BigDecimal notesTotal;
    private BigDecimal coinsTotal;
    private BigDecimal cashGivenToNextShift;
    private BigDecimal balanceWithShiftIncharge;
    private BigDecimal balanceWithCashier;

    @NotNull(message = "Status is required")
    private CashCollection.Status status;

    private Long collectedBy;
    private Long verifiedBy;
    private LocalDateTime verifiedAt;
    private String notes;

    // Additional fields for UI
    private String pumpName;
    private String shiftName;
    private String cashierName;
    private String collectedByName;
    private String verifiedByName;
}
