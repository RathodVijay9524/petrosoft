package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.Account;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountDTO {
    private Long id;
    
    @NotBlank(message = "Account code is required")
    @Size(max = 20, message = "Account code must not exceed 20 characters")
    private String accountCode;
    
    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    private String accountName;
    
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
    
    @NotNull(message = "Account group is required")
    private Account.AccountGroup accountGroup;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Builder.Default
    private BigDecimal openingBalance = BigDecimal.ZERO;
    
    @Builder.Default
    private Account.BalanceType balanceType = Account.BalanceType.DEBIT;
    
    @NotNull(message = "Pump ID is required")
    private Long pumpId;
    
    private String parentAccountCode;
    
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
    private String gstNumber;
    
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;
    
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;
    
    @Size(max = 50, message = "Bank account number must not exceed 50 characters")
    private String bankAccountNumber;
    
    @Size(max = 20, message = "IFSC code must not exceed 20 characters")
    private String ifscCode;
    
    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    private String branchName;
    
    @Builder.Default
    private boolean isSystemAccount = false;
    
    @Builder.Default
    private boolean isActive = true;
    
    @Builder.Default
    private boolean isLocked = false;
    
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal reconciledBalance = BigDecimal.ZERO;
    
    private LocalDateTime lastReconciledAt;
    
    private Map<String, Object> additionalInfo;
}