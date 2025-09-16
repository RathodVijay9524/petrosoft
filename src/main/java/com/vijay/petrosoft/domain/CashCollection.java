package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CashCollection extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pump_id", nullable = false)
    private Long pumpId;

    @Column(name = "shift_id", nullable = false)
    private Long shiftId;

    @Column(name = "cashier_id", nullable = false)
    private Long cashierId;

    @Column(name = "collection_date", nullable = false)
    private LocalDateTime collectionDate;

    @Column(name = "opening_cash", precision = 15, scale = 2)
    private BigDecimal openingCash;

    @Column(name = "closing_cash", precision = 15, scale = 2)
    private BigDecimal closingCash;

    @Column(name = "total_sales", precision = 15, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "cash_sales", precision = 15, scale = 2)
    private BigDecimal cashSales;

    @Column(name = "card_sales", precision = 15, scale = 2)
    private BigDecimal cardSales;

    @Column(name = "credit_sales", precision = 15, scale = 2)
    private BigDecimal creditSales;

    @Column(name = "cash_collected", precision = 15, scale = 2)
    private BigDecimal cashCollected;

    @Column(name = "cash_shortage", precision = 15, scale = 2)
    private BigDecimal cashShortage;

    @Column(name = "cash_excess", precision = 15, scale = 2)
    private BigDecimal cashExcess;

    @Column(name = "notes_total", precision = 15, scale = 2)
    private BigDecimal notesTotal;

    @Column(name = "coins_total", precision = 15, scale = 2)
    private BigDecimal coinsTotal;

    @Column(name = "cash_given_to_next_shift", precision = 15, scale = 2)
    private BigDecimal cashGivenToNextShift;

    @Column(name = "balance_with_shift_incharge", precision = 15, scale = 2)
    private BigDecimal balanceWithShiftIncharge;

    @Column(name = "balance_with_cashier", precision = 15, scale = 2)
    private BigDecimal balanceWithCashier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "collected_by")
    private Long collectedBy;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "notes", length = 1000)
    private String notes;

    public enum Status {
        DRAFT,
        SUBMITTED,
        VERIFIED,
        APPROVED,
        REJECTED
    }

    // Helper methods
    public void calculateCashDifference() {
        if (cashCollected != null && totalSales != null) {
            BigDecimal difference = cashCollected.subtract(totalSales);
            if (difference.compareTo(BigDecimal.ZERO) < 0) {
                this.cashShortage = difference.abs();
                this.cashExcess = BigDecimal.ZERO;
            } else {
                this.cashExcess = difference;
                this.cashShortage = BigDecimal.ZERO;
            }
        }
    }

    public void calculateTotalCash() {
        if (notesTotal != null && coinsTotal != null) {
            this.cashCollected = notesTotal.add(coinsTotal);
        }
    }
}
