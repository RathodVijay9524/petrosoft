package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialBalanceDTO {

    private Long accountId;
    private String accountCode;
    private String accountName;
    private Account.AccountType accountType;
    private String accountGroup;
    private BigDecimal openingBalance;
    private BigDecimal openingBalanceType; // 1 for Debit, -1 for Credit
    private BigDecimal currentBalance;
    private BigDecimal currentBalanceType; // 1 for Debit, -1 for Credit
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal closingBalance;
    private BigDecimal closingBalanceType; // 1 for Debit, -1 for Credit
    private Long pumpId;
    private String reportDate;
    private String financialYear;
}
