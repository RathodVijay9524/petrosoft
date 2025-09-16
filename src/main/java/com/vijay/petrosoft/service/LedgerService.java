package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.LedgerEntryDTO;
import com.vijay.petrosoft.domain.LedgerEntry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LedgerService {
    
    // CRUD Operations
    LedgerEntryDTO createLedgerEntry(LedgerEntryDTO ledgerEntryDTO);
    LedgerEntryDTO updateLedgerEntry(Long id, LedgerEntryDTO ledgerEntryDTO);
    Optional<LedgerEntryDTO> getLedgerEntryById(Long id);
    List<LedgerEntryDTO> getAllLedgerEntries();
    List<LedgerEntryDTO> getLedgerEntriesByPumpId(Long pumpId);
    void deleteLedgerEntry(Long id);
    
    // Account-based Ledger Operations
    List<LedgerEntryDTO> getLedgerEntriesByAccount(Long accountId);
    List<LedgerEntryDTO> getLedgerEntriesByAccountAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate);
    List<LedgerEntryDTO> getLedgerEntriesByAccountOrderByDate(Long accountId);
    
    // Date Range Operations
    List<LedgerEntryDTO> getLedgerEntriesByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    // Entry Type Operations
    List<LedgerEntryDTO> getLedgerEntriesByAccountAndType(Long accountId, LedgerEntry.EntryType entryType);
    List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndType(Long pumpId, LedgerEntry.EntryType entryType);
    
    // Running Balance Management
    BigDecimal calculateRunningBalance(Long accountId, LocalDate asOfDate);
    LedgerEntryDTO updateRunningBalance(Long id, BigDecimal runningBalance);
    List<LedgerEntryDTO> recalculateRunningBalances(Long accountId);
    
    // Reconciliation Operations
    List<LedgerEntryDTO> getUnreconciledEntries(Long accountId);
    List<LedgerEntryDTO> getUnreconciledEntriesByPumpId(Long pumpId);
    List<LedgerEntryDTO> getReconciledEntries(Long accountId);
    LedgerEntryDTO reconcileEntry(Long id, Long reconciledBy);
    LedgerEntryDTO unreconcileEntry(Long id);
    
    // Party Management
    List<LedgerEntryDTO> getLedgerEntriesByPartyName(Long accountId, String partyName);
    List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndPartyName(Long pumpId, String partyName);
    
    // Reference Management
    List<LedgerEntryDTO> getLedgerEntriesByReference(Long accountId, String reference);
    List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndReference(Long pumpId, String reference);
    
    // Cheque Management
    List<LedgerEntryDTO> getLedgerEntriesByChequeNumber(Long accountId, String chequeNumber);
    List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndChequeNumber(Long pumpId, String chequeNumber);
    List<LedgerEntryDTO> getLedgerEntriesByChequeDate(Long accountId, LocalDate chequeDate);
    
    // Voucher Management
    List<LedgerEntryDTO> getLedgerEntriesByVoucherNumber(Long accountId, String voucherNumber);
    List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndVoucherNumber(Long pumpId, String voucherNumber);
    
    // Amount Calculations
    BigDecimal getTotalDebitByAccount(Long accountId);
    BigDecimal getTotalCreditByAccount(Long accountId);
    BigDecimal getTotalDebitByAccountAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalCreditByAccountAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate);
    BigDecimal getCurrentBalance(Long accountId);
    BigDecimal getBalanceAsOfDate(Long accountId, LocalDate asOfDate);
    
    // Latest Entry Operations
    Optional<LedgerEntryDTO> getLatestEntryByAccount(Long accountId);
    Optional<LedgerEntryDTO> getLatestEntryByPumpId(Long pumpId);
    
    // Count Operations
    long countLedgerEntriesByAccount(Long accountId);
    long countLedgerEntriesByPumpId(Long pumpId);
    
    // Search Operations
    List<LedgerEntryDTO> searchLedgerEntriesByDescription(Long accountId, String description);
    List<LedgerEntryDTO> searchLedgerEntriesByPumpIdAndDescription(Long pumpId, String description);
    
    // Analytics and Reports
    Map<String, Object> getLedgerAnalytics(Long accountId);
    Map<String, Object> getLedgerAnalyticsByPumpId(Long pumpId);
    Map<String, BigDecimal> getLedgerSummaryByAccount(Long accountId, LocalDate startDate, LocalDate endDate);
    Map<String, BigDecimal> getLedgerSummaryByPumpId(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    // Bulk Operations
    List<LedgerEntryDTO> bulkReconcileEntries(List<Long> entryIds, Long reconciledBy);
    List<LedgerEntryDTO> bulkUnreconcileEntries(List<Long> entryIds);
    
    // Ledger Reports
    List<LedgerEntryDTO> getCustomerLedger(Long customerAccountId, LocalDate startDate, LocalDate endDate);
    List<LedgerEntryDTO> getSupplierLedger(Long supplierAccountId, LocalDate startDate, LocalDate endDate);
    List<LedgerEntryDTO> getCashierLedger(Long cashierAccountId, LocalDate startDate, LocalDate endDate);
    List<LedgerEntryDTO> getGeneralLedger(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    // Auto-generated Ledger Entries
    LedgerEntryDTO createLedgerEntryFromVoucher(Long voucherId);
}
