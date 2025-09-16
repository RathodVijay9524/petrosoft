package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.CashMovement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashMovementDTO {

    private Long id;

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    private Long fromShiftId;
    private Long toShiftId;

    @NotNull(message = "Movement type is required")
    private CashMovement.MovementType movementType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private Long currencyDenominationId;
    private Integer quantity;

    private String referenceNumber;
    private String notes;

    @NotNull(message = "Movement date is required")
    private LocalDateTime movementDate;

    private Long processedBy;
    private CashMovement.Status status;
    private Long approvedBy;
    private LocalDateTime approvedAt;

    // Additional fields for UI
    private String pumpName;
    private String shiftName;
    private String processedByName;
    private String approvedByName;
}
