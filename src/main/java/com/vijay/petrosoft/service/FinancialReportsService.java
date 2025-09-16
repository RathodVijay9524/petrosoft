package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FinancialReportsService {
    
    // Trial Balance Reports
    TrialBalanceDTO generateTrialBalance(Long pumpId, LocalDate asOfDate);
    List<TrialBalanceDTO> generateTrialBalanceDetailed(Long pumpId, LocalDate asOfDate);
    Map<String, BigDecimal> getTrialBalanceSummary(Long pumpId, LocalDate asOfDate);
    
    // Profit & Loss Reports
    ProfitLossDTO generateProfitLossStatement(Long pumpId, LocalDate fromDate, LocalDate toDate);
    ProfitLossDTO generateMonthlyProfitLoss(Long pumpId, LocalDate month);
    ProfitLossDTO generateQuarterlyProfitLoss(Long pumpId, LocalDate quarterStart);
    ProfitLossDTO generateYearlyProfitLoss(Long pumpId, String financialYear);
    
    // Balance Sheet Reports
    BalanceSheetDTO generateBalanceSheet(Long pumpId, LocalDate asOfDate);
    BalanceSheetDTO generateMonthlyBalanceSheet(Long pumpId, LocalDate month);
    BalanceSheetDTO generateYearlyBalanceSheet(Long pumpId, String financialYear);
    
    // Cash Book Reports
    CashBookDTO generateCashBook(Long pumpId, Long accountId, LocalDate fromDate, LocalDate toDate);
    CashBookDTO generateCashBookByAccountCode(Long pumpId, String accountCode, LocalDate fromDate, LocalDate toDate);
    List<CashBookDTO> generateAllCashBooks(Long pumpId, LocalDate fromDate, LocalDate toDate);
    
    // Day Book Reports
    DayBookDTO generateDayBook(Long pumpId, LocalDate reportDate);
    DayBookDTO generateDayBookRange(Long pumpId, LocalDate fromDate, LocalDate toDate);
    List<DayBookDTO> generateWeeklyDayBook(Long pumpId, LocalDate weekStart);
    List<DayBookDTO> generateMonthlyDayBook(Long pumpId, LocalDate month);
    
    // Account-specific Reports
    Map<String, BigDecimal> generateAccountSummary(Long pumpId, LocalDate fromDate, LocalDate toDate);
    Map<String, BigDecimal> generateAccountGroupSummary(Long pumpId, LocalDate fromDate, LocalDate toDate);
    Map<String, BigDecimal> generateAccountTypeSummary(Long pumpId, LocalDate fromDate, LocalDate toDate);
    
    // Comparative Reports
    ProfitLossDTO generateComparativeProfitLoss(Long pumpId, LocalDate currentFrom, LocalDate currentTo, 
                                               LocalDate previousFrom, LocalDate previousTo);
    BalanceSheetDTO generateComparativeBalanceSheet(Long pumpId, LocalDate currentDate, LocalDate previousDate);
    
    // Financial Analysis
    Map<String, Object> generateFinancialAnalysis(Long pumpId, LocalDate fromDate, LocalDate toDate);
    Map<String, BigDecimal> calculateFinancialRatios(Long pumpId, LocalDate asOfDate);
    Map<String, Object> generateCashFlowAnalysis(Long pumpId, LocalDate fromDate, LocalDate toDate);
    
    // Export and Print
    String exportTrialBalanceToCSV(Long pumpId, LocalDate asOfDate);
    String exportProfitLossToCSV(Long pumpId, LocalDate fromDate, LocalDate toDate);
    String exportBalanceSheetToCSV(Long pumpId, LocalDate asOfDate);
    String exportCashBookToCSV(Long pumpId, Long accountId, LocalDate fromDate, LocalDate toDate);
    String exportDayBookToCSV(Long pumpId, LocalDate reportDate);
    
    // Validation and Verification
    boolean validateTrialBalance(Long pumpId, LocalDate asOfDate);
    boolean validateBalanceSheet(Long pumpId, LocalDate asOfDate);
    boolean validateProfitLoss(Long pumpId, LocalDate fromDate, LocalDate toDate);
    Map<String, Object> getReportValidationStatus(Long pumpId, LocalDate fromDate, LocalDate toDate);
    
    // Report Scheduling
    void scheduleMonthlyReports(Long pumpId);
    void scheduleQuarterlyReports(Long pumpId);
    void scheduleYearlyReports(Long pumpId);
    List<Map<String, Object>> getScheduledReports(Long pumpId);
    
    // Historical Reports
    List<TrialBalanceDTO> getHistoricalTrialBalance(Long pumpId, int months);
    List<ProfitLossDTO> getHistoricalProfitLoss(Long pumpId, int months);
    List<BalanceSheetDTO> getHistoricalBalanceSheet(Long pumpId, int months);
    
    // Dashboard Analytics
    Map<String, Object> getFinancialDashboard(Long pumpId);
    Map<String, BigDecimal> getKeyFinancialMetrics(Long pumpId, LocalDate asOfDate);
    Map<String, Object> getTrendAnalysis(Long pumpId, LocalDate fromDate, LocalDate toDate);
}
