package com.vijay.petrosoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashBookDTO {

    private Long pumpId;
    private String reportTitle;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String accountName;
    private String accountCode;
    private String generatedAt;
    private String generatedBy;

    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal totalReceipts;
    private BigDecimal totalPayments;
    private BigDecimal netCashFlow;

    private List<CashBookEntryDTO> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CashBookEntryDTO {
        private LocalDateTime transactionDate;
        private String voucherNumber;
        private String narration;
        private String partyName;
        private BigDecimal receiptAmount;
        private BigDecimal paymentAmount;
        private BigDecimal runningBalance;
        private String paymentMode;
        private String reference;
        private String voucherType;
    }
}
