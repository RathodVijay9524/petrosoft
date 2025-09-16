package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.LedgerEntry;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LedgerEntryDTO {
    private Long id;
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    private Long voucherId;
    
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Size(max = 20, message = "Voucher number must not exceed 20 characters")
    private String voucherNumber;
    
    @NotNull(message = "Entry type is required")
    private LedgerEntry.EntryType entryType;
    
    @Builder.Default
    private BigDecimal debitAmount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal creditAmount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal runningBalance = BigDecimal.ZERO;
    
    @NotNull(message = "Pump ID is required")
    private Long pumpId;
    
    @Size(max = 100, message = "Party name must not exceed 100 characters")
    private String partyName;
    
    @Size(max = 20, message = "Reference must not exceed 20 characters")
    private String reference;
    
    @Size(max = 100, message = "Cheque number must not exceed 100 characters")
    private String chequeNumber;
    
    private LocalDate chequeDate;
    
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;
    
    @Builder.Default
    private boolean isReconciled = false;
    
    private LocalDateTime reconciledAt;
    
    private Map<String, Object> additionalInfo;
    
    // Additional fields for compatibility
    private BigDecimal amount;
    private String narration;
    private Long partyId;
    private Long reconciledBy;
}