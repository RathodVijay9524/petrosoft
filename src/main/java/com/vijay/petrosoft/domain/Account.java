package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false)
public class Account extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 20)
    private String accountCode;
    
    @Column(nullable = false, length = 100)
    private String accountName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountGroup accountGroup;
    
    @Column(length = 500)
    private String description;
    
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BalanceType balanceType = BalanceType.DEBIT;
    
    @Column(nullable = false)
    private Long pumpId;
    
    @Column(length = 100)
    private String parentAccountCode; // For hierarchical accounts
    
    @Column(length = 20)
    private String gstNumber;
    
    @Column(length = 20)
    private String panNumber;
    
    @Column(length = 100)
    private String bankName;
    
    @Column(length = 50)
    private String bankAccountNumber;
    
    @Column(length = 20)
    private String ifscCode;
    
    @Column(length = 100)
    private String branchName;
    
    @Builder.Default
    private boolean isSystemAccount = false; // System generated accounts
    
    @Builder.Default
    private boolean isActive = true;
    
    @Builder.Default
    private boolean isLocked = false; // Prevent modifications
    
    @Builder.Default
    private boolean isCash = false; // Cash account flag
    
    // Current balance (calculated)
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    // Balance as of last reconciliation
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal reconciledBalance = BigDecimal.ZERO;
    
    private LocalDateTime lastReconciledAt;
    
    @Column(length = 1000)
    private String additionalInfo; // JSON for custom fields
    
    public enum AccountType {
        ASSET, LIABILITY, EQUITY, INCOME, EXPENSE
    }
    
    public enum AccountGroup {
        // Assets
        CURRENT_ASSETS, FIXED_ASSETS, INVESTMENTS, OTHER_ASSETS,
        // Liabilities  
        CURRENT_LIABILITIES, LONG_TERM_LIABILITIES,
        // Equity
        CAPITAL, RESERVES_AND_SURPLUS,
        // Income
        DIRECT_INCOME, INDIRECT_INCOME,
        // Expenses
        DIRECT_EXPENSES, INDIRECT_EXPENSES
    }
    
    public enum BalanceType {
        DEBIT, CREDIT
    }
}