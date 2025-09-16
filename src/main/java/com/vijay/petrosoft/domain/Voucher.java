package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vouchers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false)
public class Voucher extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 20)
    private String voucherNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherType voucherType;
    
    @Column(nullable = false)
    private LocalDate voucherDate;
    
    @Column(length = 500)
    private String narration;
    
    @Column(length = 500)
    private String reference;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Long pumpId;
    
    @Column(nullable = false)
    private Long userId; // User who created the voucher
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VoucherStatus status = VoucherStatus.DRAFT;
    
    @Builder.Default
    private boolean isPosted = false;
    
    private LocalDateTime postedAt;
    
    private Long postedBy;
    
    @Builder.Default
    private boolean isCancelled = false;
    
    private LocalDateTime cancelledAt;
    
    private Long cancelledBy;
    
    @Column(length = 500)
    private String cancellationReason;
    
    // Voucher-specific fields
    @Column(length = 100)
    private String partyName; // Customer/Supplier name
    
    @Column(length = 20)
    private String partyAccountCode;
    
    @Column(length = 50)
    private String chequeNumber;
    
    private LocalDate chequeDate;
    
    @Column(length = 100)
    private String bankName;
    
    @Column(length = 20)
    private String paymentMode; // Cash, Cheque, Bank Transfer, etc.
    
    @Builder.Default
    private boolean isReconciled = false;
    
    private LocalDateTime reconciledAt;
    
    private Long reconciledBy;
    
    @Column(length = 1000)
    private String additionalInfo; // JSON for custom fields
    
    // Voucher entries (one-to-many)
    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VoucherEntry> voucherEntries = new ArrayList<>();
    
    public enum VoucherType {
        CUSTOMER_RECEIPT,      // Money received from customers
        PAYMENT_VOUCHER,       // Money paid to suppliers/expenses
        MISCELLANEOUS_RECEIPT, // Other income receipts
        JOURNAL_VOUCHER,       // Manual journal entries
        CASH_DEPOSIT,          // Cash deposited to bank
        CASH_WITHDRAWAL,       // Cash withdrawn from bank
        CONTRA_VOUCHER,        // Transfer between accounts
        SALES_VOUCHER,         // Sales transactions
        PURCHASE_VOUCHER,      // Purchase transactions
        CHEQUE_RETURN          // Bounced cheque entries
    }
    
    public enum VoucherStatus {
        DRAFT, APPROVED, POSTED, CANCELLED
    }
}
