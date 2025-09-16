package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.*;
import com.vijay.petrosoft.service.FinancialReportsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/financial-reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FinancialReportsController {

    private final FinancialReportsService financialReportsService;

    // Trial Balance Reports
    @GetMapping("/trial-balance/{pumpId}")
    public ResponseEntity<TrialBalanceDTO> generateTrialBalance(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            TrialBalanceDTO trialBalance = financialReportsService.generateTrialBalance(pumpId, asOfDate);
            return ResponseEntity.ok(trialBalance);
        } catch (Exception e) {
            log.error("Error generating trial balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/trial-balance/{pumpId}/detailed")
    public ResponseEntity<List<TrialBalanceDTO>> generateTrialBalanceDetailed(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            List<TrialBalanceDTO> trialBalance = financialReportsService.generateTrialBalanceDetailed(pumpId, asOfDate);
            return ResponseEntity.ok(trialBalance);
        } catch (Exception e) {
            log.error("Error generating detailed trial balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/trial-balance/{pumpId}/summary")
    public ResponseEntity<Map<String, BigDecimal>> getTrialBalanceSummary(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            Map<String, BigDecimal> summary = financialReportsService.getTrialBalanceSummary(pumpId, asOfDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error generating trial balance summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Profit & Loss Reports
    @GetMapping("/profit-loss/{pumpId}")
    public ResponseEntity<ProfitLossDTO> generateProfitLossStatement(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            ProfitLossDTO profitLoss = financialReportsService.generateProfitLossStatement(pumpId, fromDate, toDate);
            return ResponseEntity.ok(profitLoss);
        } catch (Exception e) {
            log.error("Error generating P&L statement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profit-loss/{pumpId}/monthly")
    public ResponseEntity<ProfitLossDTO> generateMonthlyProfitLoss(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        try {
            ProfitLossDTO profitLoss = financialReportsService.generateMonthlyProfitLoss(pumpId, month);
            return ResponseEntity.ok(profitLoss);
        } catch (Exception e) {
            log.error("Error generating monthly P&L: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profit-loss/{pumpId}/quarterly")
    public ResponseEntity<ProfitLossDTO> generateQuarterlyProfitLoss(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate quarterStart) {
        try {
            ProfitLossDTO profitLoss = financialReportsService.generateQuarterlyProfitLoss(pumpId, quarterStart);
            return ResponseEntity.ok(profitLoss);
        } catch (Exception e) {
            log.error("Error generating quarterly P&L: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profit-loss/{pumpId}/yearly")
    public ResponseEntity<ProfitLossDTO> generateYearlyProfitLoss(
            @PathVariable Long pumpId,
            @RequestParam String financialYear) {
        try {
            ProfitLossDTO profitLoss = financialReportsService.generateYearlyProfitLoss(pumpId, financialYear);
            return ResponseEntity.ok(profitLoss);
        } catch (Exception e) {
            log.error("Error generating yearly P&L: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Balance Sheet Reports
    @GetMapping("/balance-sheet/{pumpId}")
    public ResponseEntity<BalanceSheetDTO> generateBalanceSheet(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            BalanceSheetDTO balanceSheet = financialReportsService.generateBalanceSheet(pumpId, asOfDate);
            return ResponseEntity.ok(balanceSheet);
        } catch (Exception e) {
            log.error("Error generating balance sheet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/balance-sheet/{pumpId}/monthly")
    public ResponseEntity<BalanceSheetDTO> generateMonthlyBalanceSheet(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        try {
            BalanceSheetDTO balanceSheet = financialReportsService.generateMonthlyBalanceSheet(pumpId, month);
            return ResponseEntity.ok(balanceSheet);
        } catch (Exception e) {
            log.error("Error generating monthly balance sheet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/balance-sheet/{pumpId}/yearly")
    public ResponseEntity<BalanceSheetDTO> generateYearlyBalanceSheet(
            @PathVariable Long pumpId,
            @RequestParam String financialYear) {
        try {
            BalanceSheetDTO balanceSheet = financialReportsService.generateYearlyBalanceSheet(pumpId, financialYear);
            return ResponseEntity.ok(balanceSheet);
        } catch (Exception e) {
            log.error("Error generating yearly balance sheet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Cash Book Reports
    @GetMapping("/cash-book/{pumpId}")
    public ResponseEntity<CashBookDTO> generateCashBook(
            @PathVariable Long pumpId,
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            CashBookDTO cashBook = financialReportsService.generateCashBook(pumpId, accountId, fromDate, toDate);
            return ResponseEntity.ok(cashBook);
        } catch (Exception e) {
            log.error("Error generating cash book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cash-book/{pumpId}/by-code")
    public ResponseEntity<CashBookDTO> generateCashBookByAccountCode(
            @PathVariable Long pumpId,
            @RequestParam String accountCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            CashBookDTO cashBook = financialReportsService.generateCashBookByAccountCode(pumpId, accountCode, fromDate, toDate);
            return ResponseEntity.ok(cashBook);
        } catch (Exception e) {
            log.error("Error generating cash book by account code: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cash-book/{pumpId}/all")
    public ResponseEntity<List<CashBookDTO>> generateAllCashBooks(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            List<CashBookDTO> cashBooks = financialReportsService.generateAllCashBooks(pumpId, fromDate, toDate);
            return ResponseEntity.ok(cashBooks);
        } catch (Exception e) {
            log.error("Error generating all cash books: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Day Book Reports
    @GetMapping("/day-book/{pumpId}")
    public ResponseEntity<DayBookDTO> generateDayBook(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        try {
            DayBookDTO dayBook = financialReportsService.generateDayBook(pumpId, reportDate);
            return ResponseEntity.ok(dayBook);
        } catch (Exception e) {
            log.error("Error generating day book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/day-book/{pumpId}/range")
    public ResponseEntity<DayBookDTO> generateDayBookRange(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            DayBookDTO dayBook = financialReportsService.generateDayBookRange(pumpId, fromDate, toDate);
            return ResponseEntity.ok(dayBook);
        } catch (Exception e) {
            log.error("Error generating day book range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/day-book/{pumpId}/weekly")
    public ResponseEntity<List<DayBookDTO>> generateWeeklyDayBook(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        try {
            List<DayBookDTO> dayBooks = financialReportsService.generateWeeklyDayBook(pumpId, weekStart);
            return ResponseEntity.ok(dayBooks);
        } catch (Exception e) {
            log.error("Error generating weekly day book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/day-book/{pumpId}/monthly")
    public ResponseEntity<List<DayBookDTO>> generateMonthlyDayBook(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        try {
            List<DayBookDTO> dayBooks = financialReportsService.generateMonthlyDayBook(pumpId, month);
            return ResponseEntity.ok(dayBooks);
        } catch (Exception e) {
            log.error("Error generating monthly day book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Account Summary Reports
    @GetMapping("/account-summary/{pumpId}")
    public ResponseEntity<Map<String, BigDecimal>> generateAccountSummary(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            Map<String, BigDecimal> summary = financialReportsService.generateAccountSummary(pumpId, fromDate, toDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error generating account summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/account-group-summary/{pumpId}")
    public ResponseEntity<Map<String, BigDecimal>> generateAccountGroupSummary(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            Map<String, BigDecimal> summary = financialReportsService.generateAccountGroupSummary(pumpId, fromDate, toDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error generating account group summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/account-type-summary/{pumpId}")
    public ResponseEntity<Map<String, BigDecimal>> generateAccountTypeSummary(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            Map<String, BigDecimal> summary = financialReportsService.generateAccountTypeSummary(pumpId, fromDate, toDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error generating account type summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Comparative Reports
    @GetMapping("/comparative-profit-loss/{pumpId}")
    public ResponseEntity<ProfitLossDTO> generateComparativeProfitLoss(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentTo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousTo) {
        try {
            ProfitLossDTO comparativePL = financialReportsService.generateComparativeProfitLoss(pumpId, currentFrom, currentTo, previousFrom, previousTo);
            return ResponseEntity.ok(comparativePL);
        } catch (Exception e) {
            log.error("Error generating comparative P&L: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comparative-balance-sheet/{pumpId}")
    public ResponseEntity<BalanceSheetDTO> generateComparativeBalanceSheet(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousDate) {
        try {
            BalanceSheetDTO comparativeBS = financialReportsService.generateComparativeBalanceSheet(pumpId, currentDate, previousDate);
            return ResponseEntity.ok(comparativeBS);
        } catch (Exception e) {
            log.error("Error generating comparative balance sheet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Financial Analysis
    @GetMapping("/financial-analysis/{pumpId}")
    public ResponseEntity<Map<String, Object>> generateFinancialAnalysis(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            Map<String, Object> analysis = financialReportsService.generateFinancialAnalysis(pumpId, fromDate, toDate);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            log.error("Error generating financial analysis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/financial-ratios/{pumpId}")
    public ResponseEntity<Map<String, BigDecimal>> calculateFinancialRatios(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            Map<String, BigDecimal> ratios = financialReportsService.calculateFinancialRatios(pumpId, asOfDate);
            return ResponseEntity.ok(ratios);
        } catch (Exception e) {
            log.error("Error calculating financial ratios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cash-flow-analysis/{pumpId}")
    public ResponseEntity<Map<String, Object>> generateCashFlowAnalysis(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            Map<String, Object> analysis = financialReportsService.generateCashFlowAnalysis(pumpId, fromDate, toDate);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            log.error("Error generating cash flow analysis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Export Functions
    @GetMapping("/export/trial-balance/{pumpId}/csv")
    public ResponseEntity<String> exportTrialBalanceToCSV(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            String csvData = financialReportsService.exportTrialBalanceToCSV(pumpId, asOfDate);
            return ResponseEntity.ok(csvData);
        } catch (Exception e) {
            log.error("Error exporting trial balance to CSV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/profit-loss/{pumpId}/csv")
    public ResponseEntity<String> exportProfitLossToCSV(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            String csvData = financialReportsService.exportProfitLossToCSV(pumpId, fromDate, toDate);
            return ResponseEntity.ok(csvData);
        } catch (Exception e) {
            log.error("Error exporting P&L to CSV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/balance-sheet/{pumpId}/csv")
    public ResponseEntity<String> exportBalanceSheetToCSV(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            String csvData = financialReportsService.exportBalanceSheetToCSV(pumpId, asOfDate);
            return ResponseEntity.ok(csvData);
        } catch (Exception e) {
            log.error("Error exporting balance sheet to CSV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/cash-book/{pumpId}/csv")
    public ResponseEntity<String> exportCashBookToCSV(
            @PathVariable Long pumpId,
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            String csvData = financialReportsService.exportCashBookToCSV(pumpId, accountId, fromDate, toDate);
            return ResponseEntity.ok(csvData);
        } catch (Exception e) {
            log.error("Error exporting cash book to CSV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/day-book/{pumpId}/csv")
    public ResponseEntity<String> exportDayBookToCSV(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        try {
            String csvData = financialReportsService.exportDayBookToCSV(pumpId, reportDate);
            return ResponseEntity.ok(csvData);
        } catch (Exception e) {
            log.error("Error exporting day book to CSV: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Validation and Verification
    @GetMapping("/validate/trial-balance/{pumpId}")
    public ResponseEntity<Boolean> validateTrialBalance(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            boolean isValid = financialReportsService.validateTrialBalance(pumpId, asOfDate);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("Error validating trial balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/validate/balance-sheet/{pumpId}")
    public ResponseEntity<Boolean> validateBalanceSheet(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            boolean isValid = financialReportsService.validateBalanceSheet(pumpId, asOfDate);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("Error validating balance sheet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/validate/profit-loss/{pumpId}")
    public ResponseEntity<Boolean> validateProfitLoss(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            boolean isValid = financialReportsService.validateProfitLoss(pumpId, fromDate, toDate);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("Error validating P&L: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/validate/all/{pumpId}")
    public ResponseEntity<Map<String, Object>> getReportValidationStatus(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            Map<String, Object> status = financialReportsService.getReportValidationStatus(pumpId, fromDate, toDate);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting report validation status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Dashboard Analytics
    @GetMapping("/dashboard/{pumpId}")
    public ResponseEntity<Map<String, Object>> getFinancialDashboard(@PathVariable Long pumpId) {
        try {
            Map<String, Object> dashboard = financialReportsService.getFinancialDashboard(pumpId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error getting financial dashboard: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/key-metrics/{pumpId}")
    public ResponseEntity<Map<String, BigDecimal>> getKeyFinancialMetrics(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        try {
            Map<String, BigDecimal> metrics = financialReportsService.getKeyFinancialMetrics(pumpId, asOfDate);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error getting key financial metrics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/trend-analysis/{pumpId}")
    public ResponseEntity<Map<String, Object>> getTrendAnalysis(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            Map<String, Object> analysis = financialReportsService.getTrendAnalysis(pumpId, fromDate, toDate);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            log.error("Error getting trend analysis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Historical Reports
    @GetMapping("/historical/trial-balance/{pumpId}")
    public ResponseEntity<List<TrialBalanceDTO>> getHistoricalTrialBalance(
            @PathVariable Long pumpId,
            @RequestParam int months) {
        try {
            List<TrialBalanceDTO> historical = financialReportsService.getHistoricalTrialBalance(pumpId, months);
            return ResponseEntity.ok(historical);
        } catch (Exception e) {
            log.error("Error getting historical trial balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/historical/profit-loss/{pumpId}")
    public ResponseEntity<List<ProfitLossDTO>> getHistoricalProfitLoss(
            @PathVariable Long pumpId,
            @RequestParam int months) {
        try {
            List<ProfitLossDTO> historical = financialReportsService.getHistoricalProfitLoss(pumpId, months);
            return ResponseEntity.ok(historical);
        } catch (Exception e) {
            log.error("Error getting historical P&L: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/historical/balance-sheet/{pumpId}")
    public ResponseEntity<List<BalanceSheetDTO>> getHistoricalBalanceSheet(
            @PathVariable Long pumpId,
            @RequestParam int months) {
        try {
            List<BalanceSheetDTO> historical = financialReportsService.getHistoricalBalanceSheet(pumpId, months);
            return ResponseEntity.ok(historical);
        } catch (Exception e) {
            log.error("Error getting historical balance sheet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
