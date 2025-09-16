package com.vijay.petrosoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayBookDTO {

    private Long pumpId;
    private String reportTitle;
    private LocalDate reportDate;
    private String generatedAt;
    private String generatedBy;

    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private int totalTransactions;
    private int totalVouchers;

    private List<DayBookEntryDTO> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DayBookEntryDTO {
        private String voucherNumber;
        private String voucherType;
        private String narration;
        private String partyName;
        private BigDecimal debitAmount;
        private BigDecimal creditAmount;
        private String accountName;
        private String paymentMode;
        private String reference;
        private LocalDate voucherDate;
    }
}
