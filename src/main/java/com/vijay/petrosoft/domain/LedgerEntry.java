package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false)
public class LedgerEntry extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
    
    @Column(nullable = false)
    private LocalDate transactionDate;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 20)
    private String voucherNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType entryType;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal debitAmount = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal creditAmount = BigDecimal.ZERO;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal runningBalance = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Long pumpId;
    
    @Column(length = 100)
    private String partyName;
    
    @Column(length = 20)
    private String reference;
    
    @Column(length = 100)
    private String chequeNumber;
    
    private LocalDate chequeDate;
    
    @Column(length = 100)
    private String bankName;
    
    @Builder.Default
    private boolean isReconciled = false;
    
    private LocalDateTime reconciledAt;
    
    @Column(length = 500)
    private String additionalInfo; // JSON for custom fields
    
    // Additional fields for compatibility
    @Column(precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 500)
    private String narration;
    
    private Long partyId;
    
    private Long reconciledBy;
    
    public enum EntryType {
        DEBIT, CREDIT
    }
}