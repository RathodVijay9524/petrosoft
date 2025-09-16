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
public class ProfitLossDTO {

    private Long pumpId;
    private String reportTitle;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String financialYear;
    private String companyName;
    private String generatedAt;
    private String generatedBy;

    // Income Section
    private List<ProfitLossItemDTO> incomeItems;
    private BigDecimal totalIncome;

    // Direct Expenses Section
    private List<ProfitLossItemDTO> directExpenseItems;
    private BigDecimal totalDirectExpenses;
    private BigDecimal grossProfit;

    // Indirect Expenses Section
    private List<ProfitLossItemDTO> indirectExpenseItems;
    private BigDecimal totalIndirectExpenses;

    // Other Income Section
    private List<ProfitLossItemDTO> otherIncomeItems;
    private BigDecimal totalOtherIncome;

    // Final Calculations
    private BigDecimal netProfitBeforeTax;
    private BigDecimal taxExpense;
    private BigDecimal netProfitAfterTax;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfitLossItemDTO {
        private Long accountId;
        private String accountCode;
        private String accountName;
        private BigDecimal amount;
        private String accountGroup;
        private BigDecimal percentageOfTotal;
    }
    
    // Additional methods for compatibility
    public BigDecimal getTotalDebit() {
        return totalDirectExpenses.add(totalIndirectExpenses);
    }
    
    public BigDecimal getTotalCredit() {
        return totalIncome.add(totalOtherIncome);
    }
}
