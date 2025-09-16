package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CashMovement extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pump_id", nullable = false)
    private Long pumpId;

    @Column(name = "shift_id", nullable = false)
    private Long shiftId;

    @Column(name = "from_shift_id")
    private Long fromShiftId;

    @Column(name = "to_shift_id")
    private Long toShiftId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency_denomination_id")
    private Long currencyDenominationId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "processed_by")
    private Long processedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public enum MovementType {
        CASH_GIVEN_TO_NEXT_SHIFT,
        CASH_RECEIVED_FROM_PREVIOUS_SHIFT,
        CASH_COLLECTION_FROM_CASHIER,
        CASH_HANDOVER_TO_CASHIER,
        CASH_DEPOSIT_TO_BANK,
        CASH_WITHDRAWAL_FROM_BANK,
        CASH_ADJUSTMENT,
        CASH_SHORTAGE,
        CASH_EXCESS
    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}
