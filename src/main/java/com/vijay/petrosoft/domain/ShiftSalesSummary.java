package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_sales_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ShiftSalesSummary extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "pump_id", nullable = false)
    private Long pumpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id")
    private User cashier;

    @Column(name = "total_sales", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalSales = BigDecimal.ZERO;

    @Column(name = "cash_sales", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cashSales = BigDecimal.ZERO;

    @Column(name = "card_sales", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cardSales = BigDecimal.ZERO;

    @Column(name = "credit_sales", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal creditSales = BigDecimal.ZERO;

    @Column(name = "upi_sales", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal upiSales = BigDecimal.ZERO;

    @Column(name = "wallet_sales", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal walletSales = BigDecimal.ZERO;

    @Column(name = "total_quantity", precision = 15, scale = 3)
    @Builder.Default
    private BigDecimal totalQuantity = BigDecimal.ZERO;

    @Column(name = "total_transactions")
    @Builder.Default
    private Integer totalTransactions = 0;

    @Column(name = "cash_transactions")
    @Builder.Default
    private Integer cashTransactions = 0;

    @Column(name = "card_transactions")
    @Builder.Default
    private Integer cardTransactions = 0;

    @Column(name = "credit_transactions")
    @Builder.Default
    private Integer creditTransactions = 0;

    @Column(name = "opening_reading", precision = 10, scale = 3)
    private BigDecimal openingReading;

    @Column(name = "closing_reading", precision = 10, scale = 3)
    private BigDecimal closingReading;

    @Column(name = "sales_date", nullable = false)
    private LocalDateTime salesDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "generated_by")
    private Long generatedBy;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public enum Status {
        DRAFT,
        GENERATED,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    // Helper methods
    public void calculateTotals() {
        this.totalSales = cashSales.add(cardSales).add(creditSales).add(upiSales).add(walletSales);
        this.totalTransactions = cashTransactions + cardTransactions + creditTransactions;
    }

    public BigDecimal getTotalSalesByPaymentMethod(SaleTransaction.PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case CASH:
                return cashSales;
            case CARD:
                return cardSales;
            case CREDIT:
                return creditSales;
            case UPI:
                return upiSales;
            case WALLET:
                return walletSales;
            default:
                return BigDecimal.ZERO;
        }
    }

    public Integer getTransactionCountByPaymentMethod(SaleTransaction.PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case CASH:
                return cashTransactions;
            case CARD:
                return cardTransactions;
            case CREDIT:
                return creditTransactions;
            default:
                return 0;
        }
    }
}
