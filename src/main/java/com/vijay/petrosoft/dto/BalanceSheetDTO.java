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
public class BalanceSheetDTO {

    private Long pumpId;
    private String reportTitle;
    private LocalDate asOfDate;
    private String financialYear;
    private String companyName;
    private String generatedAt;
    private String generatedBy;

    // Assets Section
    private List<BalanceSheetItemDTO> currentAssets;
    private BigDecimal totalCurrentAssets;
    
    private List<BalanceSheetItemDTO> fixedAssets;
    private BigDecimal totalFixedAssets;
    
    private List<BalanceSheetItemDTO> otherAssets;
    private BigDecimal totalOtherAssets;
    
    private BigDecimal totalAssets;

    // Liabilities Section
    private List<BalanceSheetItemDTO> currentLiabilities;
    private BigDecimal totalCurrentLiabilities;
    
    private List<BalanceSheetItemDTO> longTermLiabilities;
    private BigDecimal totalLongTermLiabilities;
    
    private BigDecimal totalLiabilities;

    // Equity Section
    private List<BalanceSheetItemDTO> equityItems;
    private BigDecimal totalEquity;

    // Validation
    private BigDecimal netWorth;
    private boolean isBalanced; // Assets = Liabilities + Equity

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BalanceSheetItemDTO {
        private Long accountId;
        private String accountCode;
        private String accountName;
        private BigDecimal amount;
        private String accountGroup;
        private BigDecimal percentageOfTotal;
        private String notes;
    }
}
