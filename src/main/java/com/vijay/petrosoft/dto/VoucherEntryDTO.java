package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.VoucherEntry;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VoucherEntryDTO {
    private Long id;
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @Size(max = 500, message = "Narration must not exceed 500 characters")
    private String narration;
    
    @NotNull(message = "Entry type is required")
    private VoucherEntry.EntryType entryType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Builder.Default
    private BigDecimal debitAmount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal creditAmount = BigDecimal.ZERO;
    
    @Size(max = 20, message = "Reference must not exceed 20 characters")
    private String reference;
    
    @Size(max = 100, message = "Party name must not exceed 100 characters")
    private String partyName;
    
    private Map<String, Object> additionalInfo;
}
