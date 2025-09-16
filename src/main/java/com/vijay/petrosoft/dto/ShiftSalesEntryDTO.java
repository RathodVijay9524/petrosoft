package com.vijay.petrosoft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftSalesEntryDTO {

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    private Long operatorId;
    private Long cashierId;
    private LocalDateTime entryDate;

    private BigDecimal openingReading;
    private BigDecimal closingReading;
    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;

    private List<ShiftSalesItemDTO> salesItems;
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShiftSalesItemDTO {
        private Long fuelTypeId;
        private Long nozzleId;
        private BigDecimal quantity;
        private BigDecimal rate;
        private BigDecimal amount;
        private String itemName;
    }
}
