package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Account;
import com.vijay.petrosoft.domain.LedgerEntry;
import com.vijay.petrosoft.domain.MasterSetup;
import com.vijay.petrosoft.domain.Voucher;
import com.vijay.petrosoft.domain.VoucherEntry;
import com.vijay.petrosoft.dto.*;
import com.vijay.petrosoft.repository.AccountRepository;
import com.vijay.petrosoft.repository.LedgerEntryRepository;
import com.vijay.petrosoft.repository.MasterSetupRepository;
import com.vijay.petrosoft.repository.VoucherRepository;
import com.vijay.petrosoft.service.FinancialReportsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FinancialReportsServiceImpl implements FinancialReportsService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final VoucherRepository voucherRepository;
    private final MasterSetupRepository masterSetupRepository;

    @Override
    public TrialBalanceDTO generateTrialBalance(Long pumpId, LocalDate asOfDate) {
        log.info("Generating trial balance for pump ID: {} as of date: {}", pumpId, asOfDate);
        
        List<Account> accounts = accountRepository.findActiveAccountsByPumpIdOrderByCode(pumpId);
        List<TrialBalanceDTO> trialBalanceItems = new ArrayList<>();
        
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        
        for (Account account : accounts) {
            BigDecimal openingBalance = account.getOpeningBalance();
            BigDecimal currentBalance = calculateAccountBalance(account.getId(), asOfDate);
            BigDecimal closingBalance = openingBalance.add(currentBalance);
            
            // Determine balance type based on account type
            BigDecimal balanceType = getBalanceTypeMultiplier(account.getAccountType());
            BigDecimal adjustedClosingBalance = closingBalance.multiply(balanceType);
            
            TrialBalanceDTO item = TrialBalanceDTO.builder()
                    .accountId(account.getId())
                    .accountCode(account.getAccountCode())
                    .accountName(account.getAccountName())
                    .accountType(account.getAccountType())
                    .accountGroup(account.getAccountGroup().toString())
                    .openingBalance(openingBalance)
                    .openingBalanceType(balanceType)
                    .currentBalance(currentBalance)
                    .currentBalanceType(balanceType)
                    .totalDebit(adjustedClosingBalance.compareTo(BigDecimal.ZERO) > 0 ? adjustedClosingBalance : BigDecimal.ZERO)
                    .totalCredit(adjustedClosingBalance.compareTo(BigDecimal.ZERO) < 0 ? adjustedClosingBalance.abs() : BigDecimal.ZERO)
                    .closingBalance(closingBalance)
                    .closingBalanceType(balanceType)
                    .pumpId(pumpId)
                    .reportDate(asOfDate.toString())
                    .build();
            
            trialBalanceItems.add(item);
            
            totalDebit = totalDebit.add(item.getTotalDebit());
            totalCredit = totalCredit.add(item.getTotalCredit());
        }
        
        // Create summary trial balance
        TrialBalanceDTO trialBalance = TrialBalanceDTO.builder()
                .pumpId(pumpId)
                .reportDate(asOfDate.toString())
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .build();
        
        log.info("Trial balance generated with total debit: {} and total credit: {}", totalDebit, totalCredit);
        return trialBalance;
    }

    @Override
    public List<TrialBalanceDTO> generateTrialBalanceDetailed(Long pumpId, LocalDate asOfDate) {
        log.info("Generating detailed trial balance for pump ID: {} as of date: {}", pumpId, asOfDate);
        
        List<Account> accounts = accountRepository.findActiveAccountsByPumpIdOrderByCode(pumpId);
        List<TrialBalanceDTO> trialBalanceItems = new ArrayList<>();
        
        for (Account account : accounts) {
            BigDecimal openingBalance = account.getOpeningBalance();
            BigDecimal currentBalance = calculateAccountBalance(account.getId(), asOfDate);
            BigDecimal closingBalance = openingBalance.add(currentBalance);
            
            BigDecimal balanceType = getBalanceTypeMultiplier(account.getAccountType());
            BigDecimal adjustedClosingBalance = closingBalance.multiply(balanceType);
            
            TrialBalanceDTO item = TrialBalanceDTO.builder()
                    .accountId(account.getId())
                    .accountCode(account.getAccountCode())
                    .accountName(account.getAccountName())
                    .accountType(account.getAccountType())
                    .accountGroup(account.getAccountGroup().toString())
                    .openingBalance(openingBalance)
                    .openingBalanceType(balanceType)
                    .currentBalance(currentBalance)
                    .currentBalanceType(balanceType)
                    .totalDebit(adjustedClosingBalance.compareTo(BigDecimal.ZERO) > 0 ? adjustedClosingBalance : BigDecimal.ZERO)
                    .totalCredit(adjustedClosingBalance.compareTo(BigDecimal.ZERO) < 0 ? adjustedClosingBalance.abs() : BigDecimal.ZERO)
                    .closingBalance(closingBalance)
                    .closingBalanceType(balanceType)
                    .pumpId(pumpId)
                    .reportDate(asOfDate.toString())
                    .build();
            
            trialBalanceItems.add(item);
        }
        
        return trialBalanceItems;
    }

    @Override
    public Map<String, BigDecimal> getTrialBalanceSummary(Long pumpId, LocalDate asOfDate) {
        List<TrialBalanceDTO> detailedTrialBalance = generateTrialBalanceDetailed(pumpId, asOfDate);
        
        BigDecimal totalDebit = detailedTrialBalance.stream()
                .map(TrialBalanceDTO::getTotalDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCredit = detailedTrialBalance.stream()
                .map(TrialBalanceDTO::getTotalCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalDebit", totalDebit);
        summary.put("totalCredit", totalCredit);
        summary.put("difference", totalDebit.subtract(totalCredit));
        summary.put("isBalanced", totalDebit.subtract(totalCredit).abs().compareTo(BigDecimal.valueOf(0.01)) < 0 ? BigDecimal.ONE : BigDecimal.ZERO);
        
        return summary;
    }

    @Override
    public ProfitLossDTO generateProfitLossStatement(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        log.info("Generating P&L statement for pump ID: {} from {} to {}", pumpId, fromDate, toDate);
        
        MasterSetup masterSetup = masterSetupRepository.findByPumpId(pumpId).orElse(null);
        String companyName = masterSetup != null ? masterSetup.getCompanyName() : "Company";
        
        // Get Income accounts
        List<Account> incomeAccounts = accountRepository.findActiveAccountsByPumpIdAndType(pumpId, Account.AccountType.INCOME);
        List<ProfitLossDTO.ProfitLossItemDTO> incomeItems = new ArrayList<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        
        for (Account account : incomeAccounts) {
            BigDecimal amount = getAccountBalanceForPeriod(account.getId(), fromDate, toDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                ProfitLossDTO.ProfitLossItemDTO item = ProfitLossDTO.ProfitLossItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                incomeItems.add(item);
                totalIncome = totalIncome.add(amount);
            }
        }
        
        // Get Direct Expenses
        List<Account> directExpenseAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.DIRECT_EXPENSES);
        List<ProfitLossDTO.ProfitLossItemDTO> directExpenseItems = new ArrayList<>();
        BigDecimal totalDirectExpenses = BigDecimal.ZERO;
        
        for (Account account : directExpenseAccounts) {
            BigDecimal amount = getAccountBalanceForPeriod(account.getId(), fromDate, toDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                ProfitLossDTO.ProfitLossItemDTO item = ProfitLossDTO.ProfitLossItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                directExpenseItems.add(item);
                totalDirectExpenses = totalDirectExpenses.add(amount);
            }
        }
        
        BigDecimal grossProfit = totalIncome.subtract(totalDirectExpenses);
        
        // Get Indirect Expenses
        List<Account> indirectExpenseAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.INDIRECT_EXPENSES);
        List<ProfitLossDTO.ProfitLossItemDTO> indirectExpenseItems = new ArrayList<>();
        BigDecimal totalIndirectExpenses = BigDecimal.ZERO;
        
        for (Account account : indirectExpenseAccounts) {
            BigDecimal amount = getAccountBalanceForPeriod(account.getId(), fromDate, toDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                ProfitLossDTO.ProfitLossItemDTO item = ProfitLossDTO.ProfitLossItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                indirectExpenseItems.add(item);
                totalIndirectExpenses = totalIndirectExpenses.add(amount);
            }
        }
        
        // Get Other Income
        List<Account> otherIncomeAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.INDIRECT_INCOME);
        List<ProfitLossDTO.ProfitLossItemDTO> otherIncomeItems = new ArrayList<>();
        BigDecimal totalOtherIncome = BigDecimal.ZERO;
        
        for (Account account : otherIncomeAccounts) {
            BigDecimal amount = getAccountBalanceForPeriod(account.getId(), fromDate, toDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                ProfitLossDTO.ProfitLossItemDTO item = ProfitLossDTO.ProfitLossItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                otherIncomeItems.add(item);
                totalOtherIncome = totalOtherIncome.add(amount);
            }
        }
        
        BigDecimal netProfitBeforeTax = grossProfit.add(totalOtherIncome).subtract(totalIndirectExpenses);
        
        // Calculate percentages
        calculatePercentages(incomeItems, totalIncome);
        calculatePercentages(directExpenseItems, totalDirectExpenses);
        calculatePercentages(indirectExpenseItems, totalIndirectExpenses);
        calculatePercentages(otherIncomeItems, totalOtherIncome);
        
        ProfitLossDTO profitLoss = ProfitLossDTO.builder()
                .pumpId(pumpId)
                .reportTitle("Profit & Loss Statement")
                .fromDate(fromDate)
                .toDate(toDate)
                .financialYear(getFinancialYear(fromDate))
                .companyName(companyName)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .incomeItems(incomeItems)
                .totalIncome(totalIncome)
                .directExpenseItems(directExpenseItems)
                .totalDirectExpenses(totalDirectExpenses)
                .grossProfit(grossProfit)
                .indirectExpenseItems(indirectExpenseItems)
                .totalIndirectExpenses(totalIndirectExpenses)
                .otherIncomeItems(otherIncomeItems)
                .totalOtherIncome(totalOtherIncome)
                .netProfitBeforeTax(netProfitBeforeTax)
                .taxExpense(BigDecimal.ZERO) // Calculate based on tax rules
                .netProfitAfterTax(netProfitBeforeTax)
                .build();
        
        log.info("P&L statement generated with net profit: {}", netProfitBeforeTax);
        return profitLoss;
    }

    @Override
    public ProfitLossDTO generateMonthlyProfitLoss(Long pumpId, LocalDate month) {
        LocalDate fromDate = month.withDayOfMonth(1);
        LocalDate toDate = month.withDayOfMonth(month.lengthOfMonth());
        return generateProfitLossStatement(pumpId, fromDate, toDate);
    }

    @Override
    public ProfitLossDTO generateQuarterlyProfitLoss(Long pumpId, LocalDate quarterStart) {
        LocalDate fromDate = quarterStart.withDayOfMonth(1);
        LocalDate toDate = quarterStart.plusMonths(3).minusDays(1);
        return generateProfitLossStatement(pumpId, fromDate, toDate);
    }

    @Override
    public ProfitLossDTO generateYearlyProfitLoss(Long pumpId, String financialYear) {
        LocalDate fromDate = LocalDate.parse(financialYear + "-04-01");
        LocalDate toDate = fromDate.plusYears(1).minusDays(1);
        return generateProfitLossStatement(pumpId, fromDate, toDate);
    }

    @Override
    public BalanceSheetDTO generateBalanceSheet(Long pumpId, LocalDate asOfDate) {
        log.info("Generating balance sheet for pump ID: {} as of date: {}", pumpId, asOfDate);
        
        MasterSetup masterSetup = masterSetupRepository.findByPumpId(pumpId).orElse(null);
        String companyName = masterSetup != null ? masterSetup.getCompanyName() : "Company";
        
        // Current Assets
        List<Account> currentAssetAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.CURRENT_ASSETS);
        List<BalanceSheetDTO.BalanceSheetItemDTO> currentAssets = new ArrayList<>();
        BigDecimal totalCurrentAssets = BigDecimal.ZERO;
        
        for (Account account : currentAssetAccounts) {
            BigDecimal amount = getAccountBalanceAsOfDate(account.getId(), asOfDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetDTO.BalanceSheetItemDTO item = BalanceSheetDTO.BalanceSheetItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                currentAssets.add(item);
                totalCurrentAssets = totalCurrentAssets.add(amount);
            }
        }
        
        // Fixed Assets
        List<Account> fixedAssetAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.FIXED_ASSETS);
        List<BalanceSheetDTO.BalanceSheetItemDTO> fixedAssets = new ArrayList<>();
        BigDecimal totalFixedAssets = BigDecimal.ZERO;
        
        for (Account account : fixedAssetAccounts) {
            BigDecimal amount = getAccountBalanceAsOfDate(account.getId(), asOfDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetDTO.BalanceSheetItemDTO item = BalanceSheetDTO.BalanceSheetItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                fixedAssets.add(item);
                totalFixedAssets = totalFixedAssets.add(amount);
            }
        }
        
        // Other Assets
        List<Account> otherAssetAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.OTHER_ASSETS);
        List<BalanceSheetDTO.BalanceSheetItemDTO> otherAssets = new ArrayList<>();
        BigDecimal totalOtherAssets = BigDecimal.ZERO;
        
        for (Account account : otherAssetAccounts) {
            BigDecimal amount = getAccountBalanceAsOfDate(account.getId(), asOfDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetDTO.BalanceSheetItemDTO item = BalanceSheetDTO.BalanceSheetItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                otherAssets.add(item);
                totalOtherAssets = totalOtherAssets.add(amount);
            }
        }
        
        BigDecimal totalAssets = totalCurrentAssets.add(totalFixedAssets).add(totalOtherAssets);
        
        // Current Liabilities
        List<Account> currentLiabilityAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.CURRENT_LIABILITIES);
        List<BalanceSheetDTO.BalanceSheetItemDTO> currentLiabilities = new ArrayList<>();
        BigDecimal totalCurrentLiabilities = BigDecimal.ZERO;
        
        for (Account account : currentLiabilityAccounts) {
            BigDecimal amount = getAccountBalanceAsOfDate(account.getId(), asOfDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetDTO.BalanceSheetItemDTO item = BalanceSheetDTO.BalanceSheetItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount.abs())
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                currentLiabilities.add(item);
                totalCurrentLiabilities = totalCurrentLiabilities.add(amount.abs());
            }
        }
        
        // Long Term Liabilities
        List<Account> longTermLiabilityAccounts = accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, Account.AccountGroup.LONG_TERM_LIABILITIES);
        List<BalanceSheetDTO.BalanceSheetItemDTO> longTermLiabilities = new ArrayList<>();
        BigDecimal totalLongTermLiabilities = BigDecimal.ZERO;
        
        for (Account account : longTermLiabilityAccounts) {
            BigDecimal amount = getAccountBalanceAsOfDate(account.getId(), asOfDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetDTO.BalanceSheetItemDTO item = BalanceSheetDTO.BalanceSheetItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount.abs())
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                longTermLiabilities.add(item);
                totalLongTermLiabilities = totalLongTermLiabilities.add(amount.abs());
            }
        }
        
        BigDecimal totalLiabilities = totalCurrentLiabilities.add(totalLongTermLiabilities);
        
        // Equity
        List<Account> equityAccounts = accountRepository.findActiveAccountsByPumpIdAndType(pumpId, Account.AccountType.EQUITY);
        List<BalanceSheetDTO.BalanceSheetItemDTO> equityItems = new ArrayList<>();
        BigDecimal totalEquity = BigDecimal.ZERO;
        
        for (Account account : equityAccounts) {
            BigDecimal amount = getAccountBalanceAsOfDate(account.getId(), asOfDate);
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetDTO.BalanceSheetItemDTO item = BalanceSheetDTO.BalanceSheetItemDTO.builder()
                        .accountId(account.getId())
                        .accountCode(account.getAccountCode())
                        .accountName(account.getAccountName())
                        .amount(amount)
                        .accountGroup(account.getAccountGroup().toString())
                        .build();
                equityItems.add(item);
                totalEquity = totalEquity.add(amount);
            }
        }
        
        BigDecimal netWorth = totalAssets.subtract(totalLiabilities);
        boolean isBalanced = totalAssets.subtract(totalLiabilities.add(totalEquity)).abs().compareTo(BigDecimal.valueOf(0.01)) < 0;
        
        // Calculate percentages
        calculateBalanceSheetPercentages(currentAssets, totalAssets);
        calculateBalanceSheetPercentages(fixedAssets, totalAssets);
        calculateBalanceSheetPercentages(otherAssets, totalAssets);
        calculateBalanceSheetPercentages(currentLiabilities, totalAssets);
        calculateBalanceSheetPercentages(longTermLiabilities, totalAssets);
        calculateBalanceSheetPercentages(equityItems, totalAssets);
        
        BalanceSheetDTO balanceSheet = BalanceSheetDTO.builder()
                .pumpId(pumpId)
                .reportTitle("Balance Sheet")
                .asOfDate(asOfDate)
                .financialYear(getFinancialYear(asOfDate))
                .companyName(companyName)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .currentAssets(currentAssets)
                .totalCurrentAssets(totalCurrentAssets)
                .fixedAssets(fixedAssets)
                .totalFixedAssets(totalFixedAssets)
                .otherAssets(otherAssets)
                .totalOtherAssets(totalOtherAssets)
                .totalAssets(totalAssets)
                .currentLiabilities(currentLiabilities)
                .totalCurrentLiabilities(totalCurrentLiabilities)
                .longTermLiabilities(longTermLiabilities)
                .totalLongTermLiabilities(totalLongTermLiabilities)
                .totalLiabilities(totalLiabilities)
                .equityItems(equityItems)
                .totalEquity(totalEquity)
                .netWorth(netWorth)
                .isBalanced(isBalanced)
                .build();
        
        log.info("Balance sheet generated with total assets: {} and total liabilities: {}", totalAssets, totalLiabilities);
        return balanceSheet;
    }

    @Override
    public BalanceSheetDTO generateMonthlyBalanceSheet(Long pumpId, LocalDate month) {
        LocalDate asOfDate = month.withDayOfMonth(month.lengthOfMonth());
        return generateBalanceSheet(pumpId, asOfDate);
    }

    @Override
    public BalanceSheetDTO generateYearlyBalanceSheet(Long pumpId, String financialYear) {
        LocalDate asOfDate = LocalDate.parse(financialYear + "-03-31");
        return generateBalanceSheet(pumpId, asOfDate);
    }

    @Override
    public CashBookDTO generateCashBook(Long pumpId, Long accountId, LocalDate fromDate, LocalDate toDate) {
        log.info("Generating cash book for account ID: {} from {} to {}", accountId, fromDate, toDate);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        BigDecimal openingBalance = getAccountBalanceAsOfDate(accountId, fromDate.minusDays(1));
        
        List<LedgerEntry> entries = ledgerEntryRepository.findByAccountIdAndTransactionDateBetween(accountId, fromDate, toDate);
        List<CashBookDTO.CashBookEntryDTO> cashBookEntries = new ArrayList<>();
        
        BigDecimal runningBalance = openingBalance;
        BigDecimal totalReceipts = BigDecimal.ZERO;
        BigDecimal totalPayments = BigDecimal.ZERO;
        
        for (LedgerEntry entry : entries) {
            BigDecimal receiptAmount = BigDecimal.ZERO;
            BigDecimal paymentAmount = BigDecimal.ZERO;
            
            if (entry.getEntryType() == LedgerEntry.EntryType.DEBIT) {
                receiptAmount = entry.getAmount();
                totalReceipts = totalReceipts.add(receiptAmount);
                runningBalance = runningBalance.add(receiptAmount);
            } else {
                paymentAmount = entry.getAmount();
                totalPayments = totalPayments.add(paymentAmount);
                runningBalance = runningBalance.subtract(paymentAmount);
            }
            
            CashBookDTO.CashBookEntryDTO cashEntry = CashBookDTO.CashBookEntryDTO.builder()
                    .transactionDate(entry.getTransactionDate().atStartOfDay())
                    .voucherNumber(entry.getVoucher() != null ? entry.getVoucher().getVoucherNumber() : "")
                    .narration(entry.getNarration())
                    .partyName(entry.getPartyName())
                    .receiptAmount(receiptAmount)
                    .paymentAmount(paymentAmount)
                    .runningBalance(runningBalance)
                    .reference(entry.getReference())
                    .voucherType(entry.getVoucher() != null ? entry.getVoucher().getVoucherType().name() : "")
                    .build();
            
            cashBookEntries.add(cashEntry);
        }
        
        BigDecimal netCashFlow = totalReceipts.subtract(totalPayments);
        BigDecimal closingBalance = openingBalance.add(netCashFlow);
        
        CashBookDTO cashBook = CashBookDTO.builder()
                .pumpId(pumpId)
                .reportTitle("Cash Book - " + account.getAccountName())
                .fromDate(fromDate)
                .toDate(toDate)
                .accountName(account.getAccountName())
                .accountCode(account.getAccountCode())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .openingBalance(openingBalance)
                .closingBalance(closingBalance)
                .totalReceipts(totalReceipts)
                .totalPayments(totalPayments)
                .netCashFlow(netCashFlow)
                .entries(cashBookEntries)
                .build();
        
        log.info("Cash book generated with opening balance: {} and closing balance: {}", openingBalance, closingBalance);
        return cashBook;
    }

    @Override
    public CashBookDTO generateCashBookByAccountCode(Long pumpId, String accountCode, LocalDate fromDate, LocalDate toDate) {
        Account account = accountRepository.findByAccountCodeAndPumpId(accountCode, pumpId)
                .orElseThrow(() -> new RuntimeException("Account not found with code: " + accountCode));
        return generateCashBook(pumpId, account.getId(), fromDate, toDate);
    }

    @Override
    public List<CashBookDTO> generateAllCashBooks(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        List<Account> cashAccounts = accountRepository.findCashAccountsByPumpId(pumpId);
        List<CashBookDTO> cashBooks = new ArrayList<>();
        
        for (Account account : cashAccounts) {
            CashBookDTO cashBook = generateCashBook(pumpId, account.getId(), fromDate, toDate);
            cashBooks.add(cashBook);
        }
        
        return cashBooks;
    }

    @Override
    public DayBookDTO generateDayBook(Long pumpId, LocalDate reportDate) {
        log.info("Generating day book for pump ID: {} on date: {}", pumpId, reportDate);
        
        List<Voucher> vouchers = voucherRepository.findPostedVouchersByPumpIdAndDateOrderByDateDesc(pumpId, reportDate);
        List<DayBookDTO.DayBookEntryDTO> entries = new ArrayList<>();
        
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        int totalTransactions = 0;
        
        for (Voucher voucher : vouchers) {
            for (var voucherEntry : voucher.getVoucherEntries()) {
                BigDecimal debitAmount = voucherEntry.getEntryType() == VoucherEntry.EntryType.DEBIT ? 
                    voucherEntry.getAmount() : BigDecimal.ZERO;
                BigDecimal creditAmount = voucherEntry.getEntryType() == VoucherEntry.EntryType.CREDIT ? 
                    voucherEntry.getAmount() : BigDecimal.ZERO;
                
                DayBookDTO.DayBookEntryDTO entry = DayBookDTO.DayBookEntryDTO.builder()
                        .voucherNumber(voucher.getVoucherNumber())
                        .voucherType(voucher.getVoucherType().name())
                        .narration(voucherEntry.getNarration())
                        .partyName(voucherEntry.getPartyName())
                        .debitAmount(debitAmount)
                        .creditAmount(creditAmount)
                        .accountName(voucherEntry.getAccount().getAccountName())
                        .paymentMode(voucher.getPaymentMode())
                        .reference(voucherEntry.getReference())
                        .voucherDate(voucher.getVoucherDate())
                        .build();
                
                entries.add(entry);
                totalDebit = totalDebit.add(debitAmount);
                totalCredit = totalCredit.add(creditAmount);
                totalTransactions++;
            }
        }
        
        DayBookDTO dayBook = DayBookDTO.builder()
                .pumpId(pumpId)
                .reportTitle("Day Book - " + reportDate.toString())
                .reportDate(reportDate)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .totalTransactions(totalTransactions)
                .totalVouchers(vouchers.size())
                .entries(entries)
                .build();
        
        log.info("Day book generated with {} transactions and {} vouchers", totalTransactions, vouchers.size());
        return dayBook;
    }

    @Override
    public DayBookDTO generateDayBookRange(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        log.info("Generating day book for pump ID: {} from {} to {}", pumpId, fromDate, toDate);
        
        List<Voucher> vouchers = voucherRepository.findPostedVouchersByPumpIdAndDateRangeOrderByDateDesc(pumpId, fromDate, toDate);
        List<DayBookDTO.DayBookEntryDTO> entries = new ArrayList<>();
        
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        int totalTransactions = 0;
        
        for (Voucher voucher : vouchers) {
            for (var voucherEntry : voucher.getVoucherEntries()) {
                BigDecimal debitAmount = voucherEntry.getEntryType() == VoucherEntry.EntryType.DEBIT ? 
                    voucherEntry.getAmount() : BigDecimal.ZERO;
                BigDecimal creditAmount = voucherEntry.getEntryType() == VoucherEntry.EntryType.CREDIT ? 
                    voucherEntry.getAmount() : BigDecimal.ZERO;
                
                DayBookDTO.DayBookEntryDTO entry = DayBookDTO.DayBookEntryDTO.builder()
                        .voucherNumber(voucher.getVoucherNumber())
                        .voucherType(voucher.getVoucherType().name())
                        .narration(voucherEntry.getNarration())
                        .partyName(voucherEntry.getPartyName())
                        .debitAmount(debitAmount)
                        .creditAmount(creditAmount)
                        .accountName(voucherEntry.getAccount().getAccountName())
                        .paymentMode(voucher.getPaymentMode())
                        .reference(voucherEntry.getReference())
                        .voucherDate(voucher.getVoucherDate())
                        .build();
                
                entries.add(entry);
                totalDebit = totalDebit.add(debitAmount);
                totalCredit = totalCredit.add(creditAmount);
                totalTransactions++;
            }
        }
        
        DayBookDTO dayBook = DayBookDTO.builder()
                .pumpId(pumpId)
                .reportTitle("Day Book - " + fromDate.toString() + " to " + toDate.toString())
                .reportDate(fromDate)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .totalTransactions(totalTransactions)
                .totalVouchers(vouchers.size())
                .entries(entries)
                .build();
        
        log.info("Day book generated with {} transactions and {} vouchers", totalTransactions, vouchers.size());
        return dayBook;
    }

    @Override
    public List<DayBookDTO> generateWeeklyDayBook(Long pumpId, LocalDate weekStart) {
        List<DayBookDTO> weeklyDayBooks = new ArrayList<>();
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            DayBookDTO dayBook = generateDayBook(pumpId, date);
            weeklyDayBooks.add(dayBook);
        }
        
        return weeklyDayBooks;
    }

    @Override
    public List<DayBookDTO> generateMonthlyDayBook(Long pumpId, LocalDate month) {
        List<DayBookDTO> monthlyDayBooks = new ArrayList<>();
        
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayBookDTO dayBook = generateDayBook(pumpId, date);
            monthlyDayBooks.add(dayBook);
        }
        
        return monthlyDayBooks;
    }

    // Helper methods
    private BigDecimal calculateAccountBalance(Long accountId, LocalDate asOfDate) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByAccountIdAndTransactionDateLessThanEqualOrderByTransactionDateAsc(accountId, asOfDate);
        Account account = accountRepository.findById(accountId).orElse(null);
        
        BigDecimal balance = account != null ? account.getOpeningBalance() : BigDecimal.ZERO;
        
        for (LedgerEntry entry : entries) {
            if (entry.getEntryType() == LedgerEntry.EntryType.DEBIT) {
                balance = balance.add(entry.getAmount());
            } else {
                balance = balance.subtract(entry.getAmount());
            }
        }
        
        return balance;
    }

    private BigDecimal getAccountBalanceForPeriod(Long accountId, LocalDate fromDate, LocalDate toDate) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByAccountIdAndTransactionDateBetween(accountId, fromDate, toDate);
        BigDecimal balance = BigDecimal.ZERO;
        
        for (LedgerEntry entry : entries) {
            if (entry.getEntryType() == LedgerEntry.EntryType.DEBIT) {
                balance = balance.add(entry.getAmount());
            } else {
                balance = balance.subtract(entry.getAmount());
            }
        }
        
        return balance;
    }

    private BigDecimal getAccountBalanceAsOfDate(Long accountId, LocalDate asOfDate) {
        return calculateAccountBalance(accountId, asOfDate);
    }

    private BigDecimal getBalanceTypeMultiplier(Account.AccountType accountType) {
        switch (accountType) {
            case ASSET:
            case EXPENSE:
                return BigDecimal.ONE; // Debit balance
            case LIABILITY:
            case EQUITY:
            case INCOME:
                return BigDecimal.valueOf(-1); // Credit balance
            default:
                return BigDecimal.ONE;
        }
    }

    private void calculatePercentages(List<ProfitLossDTO.ProfitLossItemDTO> items, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return;
        
        for (ProfitLossDTO.ProfitLossItemDTO item : items) {
            BigDecimal percentage = item.getAmount().divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            item.setPercentageOfTotal(percentage);
        }
    }

    private void calculateBalanceSheetPercentages(List<BalanceSheetDTO.BalanceSheetItemDTO> items, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return;
        
        for (BalanceSheetDTO.BalanceSheetItemDTO item : items) {
            BigDecimal percentage = item.getAmount().divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            item.setPercentageOfTotal(percentage);
        }
    }

    private String getFinancialYear(LocalDate date) {
        if (date.getMonthValue() >= 4) {
            return date.getYear() + "-" + (date.getYear() + 1);
        } else {
            return (date.getYear() - 1) + "-" + date.getYear();
        }
    }

    // Placeholder implementations for remaining methods
    @Override
    public Map<String, BigDecimal> generateAccountSummary(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public Map<String, BigDecimal> generateAccountGroupSummary(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public Map<String, BigDecimal> generateAccountTypeSummary(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public ProfitLossDTO generateComparativeProfitLoss(Long pumpId, LocalDate currentFrom, LocalDate currentTo, LocalDate previousFrom, LocalDate previousTo) {
        // Implementation placeholder
        return null;
    }

    @Override
    public BalanceSheetDTO generateComparativeBalanceSheet(Long pumpId, LocalDate currentDate, LocalDate previousDate) {
        // Implementation placeholder
        return null;
    }

    @Override
    public Map<String, Object> generateFinancialAnalysis(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public Map<String, BigDecimal> calculateFinancialRatios(Long pumpId, LocalDate asOfDate) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> generateCashFlowAnalysis(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public String exportTrialBalanceToCSV(Long pumpId, LocalDate asOfDate) {
        // Implementation placeholder
        return "";
    }

    @Override
    public String exportProfitLossToCSV(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return "";
    }

    @Override
    public String exportBalanceSheetToCSV(Long pumpId, LocalDate asOfDate) {
        // Implementation placeholder
        return "";
    }

    @Override
    public String exportCashBookToCSV(Long pumpId, Long accountId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return "";
    }

    @Override
    public String exportDayBookToCSV(Long pumpId, LocalDate reportDate) {
        // Implementation placeholder
        return "";
    }

    @Override
    public boolean validateTrialBalance(Long pumpId, LocalDate asOfDate) {
        Map<String, BigDecimal> summary = getTrialBalanceSummary(pumpId, asOfDate);
        return summary.get("isBalanced").equals(BigDecimal.ONE);
    }

    @Override
    public boolean validateBalanceSheet(Long pumpId, LocalDate asOfDate) {
        BalanceSheetDTO balanceSheet = generateBalanceSheet(pumpId, asOfDate);
        return balanceSheet.isBalanced();
    }

    @Override
    public boolean validateProfitLoss(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        ProfitLossDTO profitLoss = generateProfitLossStatement(pumpId, fromDate, toDate);
        return profitLoss.getTotalDebit().equals(profitLoss.getTotalCredit());
    }

    @Override
    public Map<String, Object> getReportValidationStatus(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        Map<String, Object> status = new HashMap<>();
        status.put("trialBalanceValid", validateTrialBalance(pumpId, toDate));
        status.put("balanceSheetValid", validateBalanceSheet(pumpId, toDate));
        status.put("profitLossValid", validateProfitLoss(pumpId, fromDate, toDate));
        return status;
    }

    @Override
    public void scheduleMonthlyReports(Long pumpId) {
        // Implementation placeholder
    }

    @Override
    public void scheduleQuarterlyReports(Long pumpId) {
        // Implementation placeholder
    }

    @Override
    public void scheduleYearlyReports(Long pumpId) {
        // Implementation placeholder
    }

    @Override
    public List<Map<String, Object>> getScheduledReports(Long pumpId) {
        // Implementation placeholder
        return new ArrayList<>();
    }

    @Override
    public List<TrialBalanceDTO> getHistoricalTrialBalance(Long pumpId, int months) {
        // Implementation placeholder
        return new ArrayList<>();
    }

    @Override
    public List<ProfitLossDTO> getHistoricalProfitLoss(Long pumpId, int months) {
        // Implementation placeholder
        return new ArrayList<>();
    }

    @Override
    public List<BalanceSheetDTO> getHistoricalBalanceSheet(Long pumpId, int months) {
        // Implementation placeholder
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getFinancialDashboard(Long pumpId) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public Map<String, BigDecimal> getKeyFinancialMetrics(Long pumpId, LocalDate asOfDate) {
        // Implementation placeholder
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getTrendAnalysis(Long pumpId, LocalDate fromDate, LocalDate toDate) {
        // Implementation placeholder
        return new HashMap<>();
    }
}
