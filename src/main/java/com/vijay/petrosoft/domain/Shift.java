package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@EqualsAndHashCode(callSuper = false)
public class Shift extends Auditable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pump_id", nullable = false)
    private Long pumpId;
    
    @Column(name = "operator_id", nullable = false)
    private Long operatorId;
    
    @Column(name = "cashier_id")
    private Long cashierId;
    
    @Column(name = "shift_name", length = 50, nullable = false)
    private String shiftName; // I, II, III, etc.
    
    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;
    
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.OPEN;
    
    @Column(name = "opening_cash", precision = 15, scale = 2)
    private BigDecimal openingCash;
    
    @Column(name = "closing_cash", precision = 15, scale = 2)
    private BigDecimal closingCash;
    
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
    
    @Column(name = "cash_collected", precision = 15, scale = 2)
    private BigDecimal cashCollected;
    
    @Column(name = "cash_given_to_next_shift", precision = 15, scale = 2)
    private BigDecimal cashGivenToNextShift;
    
    @Column(name = "balance_with_shift_incharge", precision = 15, scale = 2)
    private BigDecimal balanceWithShiftIncharge;
    
    @Column(name = "balance_with_cashier", precision = 15, scale = 2)
    private BigDecimal balanceWithCashier;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "closed_by")
    private Long closedBy;
    
    public enum Status {
        OPEN,
        CLOSED,
        SUSPENDED,
        CANCELLED
    }
}
