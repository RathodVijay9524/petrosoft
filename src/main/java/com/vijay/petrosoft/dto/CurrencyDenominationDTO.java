package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.CurrencyDenomination;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyDenominationDTO {

    private Long id;

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Currency type is required")
    private CurrencyDenomination.CurrencyType currencyType;

    @NotNull(message = "Denomination value is required")
    @Positive(message = "Denomination value must be positive")
    private BigDecimal denominationValue;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    private Long countedBy;
    private Long verifiedBy;
    private String notes;

    // Additional fields for UI
    private String pumpName;
    private String shiftName;
    private String countedByName;
    private String verifiedByName;
}
