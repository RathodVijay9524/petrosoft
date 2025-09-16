package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.Voucher;
import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VoucherDTO {
    private Long id;
    
    @Size(max = 20, message = "Voucher number must not exceed 20 characters")
    private String voucherNumber;
    
    @NotNull(message = "Voucher type is required")
    private Voucher.VoucherType voucherType;
    
    @NotNull(message = "Voucher date is required")
    private LocalDate voucherDate;
    
    @Size(max = 500, message = "Narration must not exceed 500 characters")
    private String narration;
    
    @Size(max = 500, message = "Reference must not exceed 500 characters")
    private String reference;
    
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @NotNull(message = "Pump ID is required")
    private Long pumpId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @Builder.Default
    private Voucher.VoucherStatus status = Voucher.VoucherStatus.DRAFT;
    
    @Builder.Default
    private boolean isPosted = false;
    
    private LocalDateTime postedAt;
    private Long postedBy;
    
    @Builder.Default
    private boolean isCancelled = false;
    
    private LocalDateTime cancelledAt;
    private Long cancelledBy;
    
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    private String cancellationReason;
    
    // Voucher-specific fields
    @Size(max = 100, message = "Party name must not exceed 100 characters")
    private String partyName;
    
    @Size(max = 20, message = "Party account code must not exceed 20 characters")
    private String partyAccountCode;
    
    @Size(max = 50, message = "Cheque number must not exceed 50 characters")
    private String chequeNumber;
    
    private LocalDate chequeDate;
    
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;
    
    @Size(max = 20, message = "Payment mode must not exceed 20 characters")
    private String paymentMode;
    
    @Builder.Default
    private boolean isReconciled = false;
    
    private LocalDateTime reconciledAt;
    private Long reconciledBy;
    
    private Map<String, Object> additionalInfo;
    
    // Voucher entries
    @Valid
    @NotEmpty(message = "At least one voucher entry is required")
    private List<VoucherEntryDTO> voucherEntries;
}
