package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.domain.LedgerEntry;
import com.vijay.petrosoft.dto.LedgerEntryDTO;
import com.vijay.petrosoft.service.LedgerService;
import jakarta.validation.Valid;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LedgerController {

    private final LedgerService ledgerService;

    // CRUD Operations
    @PostMapping("/entries")
    public ResponseEntity<LedgerEntryDTO> createLedgerEntry(@Valid @RequestBody LedgerEntryDTO ledgerEntryDTO) {
        try {
            LedgerEntryDTO createdEntry = ledgerService.createLedgerEntry(ledgerEntryDTO);
            log.info("Ledger entry created for account ID: {}", ledgerEntryDTO.getAccountId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (Exception e) {
            log.error("Error creating ledger entry: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<LedgerEntryDTO> getLedgerEntryById(@PathVariable Long id) {
        Optional<LedgerEntryDTO> entry = ledgerService.getLedgerEntryById(id);
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/entries")
    public ResponseEntity<List<LedgerEntryDTO>> getAllLedgerEntries() {
        List<LedgerEntryDTO> entries = ledgerService.getAllLedgerEntries();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/pump/{pumpId}")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByPumpId(@PathVariable Long pumpId) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByPumpId(pumpId);
        return ResponseEntity.ok(entries);
    }

    @PutMapping("/entries/{id}")
    public ResponseEntity<LedgerEntryDTO> updateLedgerEntry(@PathVariable Long id, @Valid @RequestBody LedgerEntryDTO ledgerEntryDTO) {
        try {
            LedgerEntryDTO updatedEntry = ledgerService.updateLedgerEntry(id, ledgerEntryDTO);
            log.info("Ledger entry updated for ID: {}", id);
            return ResponseEntity.ok(updatedEntry);
        } catch (Exception e) {
            log.error("Error updating ledger entry: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteLedgerEntry(@PathVariable Long id) {
        try {
            ledgerService.deleteLedgerEntry(id);
            log.info("Ledger entry deleted for ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting ledger entry: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Account-based Ledger Operations
    @GetMapping("/entries/account/{accountId}")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByAccount(@PathVariable Long accountId) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByAccount(accountId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/account/{accountId}/date-range")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByAccountAndDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByAccountAndDateRange(accountId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/account/{accountId}/ordered")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByAccountOrderByDate(@PathVariable Long accountId) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByAccountOrderByDate(accountId);
        return ResponseEntity.ok(entries);
    }

    // Date Range Operations
    @GetMapping("/entries/date-range")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByDateRange(
            @RequestParam Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    // Entry Type Operations
    @GetMapping("/entries/account/{accountId}/type/{entryType}")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByAccountAndType(
            @PathVariable Long accountId, 
            @PathVariable LedgerEntry.EntryType entryType) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByAccountAndType(accountId, entryType);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/pump/{pumpId}/type/{entryType}")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByPumpIdAndType(
            @PathVariable Long pumpId, 
            @PathVariable LedgerEntry.EntryType entryType) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByPumpIdAndType(pumpId, entryType);
        return ResponseEntity.ok(entries);
    }

    // Running Balance Management
    @GetMapping("/balance/{accountId}")
    public ResponseEntity<BigDecimal> calculateRunningBalance(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        BigDecimal balance = ledgerService.calculateRunningBalance(accountId, asOfDate);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/entries/{id}/balance")
    public ResponseEntity<LedgerEntryDTO> updateRunningBalance(
            @PathVariable Long id, 
            @RequestParam BigDecimal runningBalance) {
        try {
            LedgerEntryDTO updatedEntry = ledgerService.updateRunningBalance(id, runningBalance);
            return ResponseEntity.ok(updatedEntry);
        } catch (Exception e) {
            log.error("Error updating running balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/entries/account/{accountId}/recalculate")
    public ResponseEntity<List<LedgerEntryDTO>> recalculateRunningBalances(@PathVariable Long accountId) {
        try {
            List<LedgerEntryDTO> updatedEntries = ledgerService.recalculateRunningBalances(accountId);
            return ResponseEntity.ok(updatedEntries);
        } catch (Exception e) {
            log.error("Error recalculating running balances: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Reconciliation Operations
    @GetMapping("/entries/unreconciled/account/{accountId}")
    public ResponseEntity<List<LedgerEntryDTO>> getUnreconciledEntries(@PathVariable Long accountId) {
        List<LedgerEntryDTO> entries = ledgerService.getUnreconciledEntries(accountId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/unreconciled/pump/{pumpId}")
    public ResponseEntity<List<LedgerEntryDTO>> getUnreconciledEntriesByPumpId(@PathVariable Long pumpId) {
        List<LedgerEntryDTO> entries = ledgerService.getUnreconciledEntriesByPumpId(pumpId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/reconciled/account/{accountId}")
    public ResponseEntity<List<LedgerEntryDTO>> getReconciledEntries(@PathVariable Long accountId) {
        List<LedgerEntryDTO> entries = ledgerService.getReconciledEntries(accountId);
        return ResponseEntity.ok(entries);
    }

    @PutMapping("/entries/{id}/reconcile")
    public ResponseEntity<LedgerEntryDTO> reconcileEntry(
            @PathVariable Long id, 
            @RequestParam Long reconciledBy) {
        try {
            LedgerEntryDTO reconciledEntry = ledgerService.reconcileEntry(id, reconciledBy);
            return ResponseEntity.ok(reconciledEntry);
        } catch (Exception e) {
            log.error("Error reconciling ledger entry: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/entries/{id}/unreconcile")
    public ResponseEntity<LedgerEntryDTO> unreconcileEntry(@PathVariable Long id) {
        try {
            LedgerEntryDTO unreconciledEntry = ledgerService.unreconcileEntry(id);
            return ResponseEntity.ok(unreconciledEntry);
        } catch (Exception e) {
            log.error("Error unreconciling ledger entry: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Party Management
    @GetMapping("/entries/party")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByPartyName(
            @RequestParam Long accountId,
            @RequestParam String partyName) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByPartyName(accountId, partyName);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/pump/{pumpId}/party")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByPumpIdAndPartyName(
            @PathVariable Long pumpId,
            @RequestParam String partyName) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByPumpIdAndPartyName(pumpId, partyName);
        return ResponseEntity.ok(entries);
    }

    // Reference Management
    @GetMapping("/entries/reference")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByReference(
            @RequestParam Long accountId,
            @RequestParam String reference) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByReference(accountId, reference);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/pump/{pumpId}/reference")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByPumpIdAndReference(
            @PathVariable Long pumpId,
            @RequestParam String reference) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByPumpIdAndReference(pumpId, reference);
        return ResponseEntity.ok(entries);
    }

    // Cheque Management
    @GetMapping("/entries/cheque")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByChequeNumber(
            @RequestParam Long accountId,
            @RequestParam String chequeNumber) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByChequeNumber(accountId, chequeNumber);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/pump/{pumpId}/cheque")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByPumpIdAndChequeNumber(
            @PathVariable Long pumpId,
            @RequestParam String chequeNumber) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByPumpIdAndChequeNumber(pumpId, chequeNumber);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/cheque-date")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByChequeDate(
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate chequeDate) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByChequeDate(accountId, chequeDate);
        return ResponseEntity.ok(entries);
    }

    // Voucher Management
    @GetMapping("/entries/voucher")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByVoucherNumber(
            @RequestParam Long accountId,
            @RequestParam String voucherNumber) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByVoucherNumber(accountId, voucherNumber);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/pump/{pumpId}/voucher")
    public ResponseEntity<List<LedgerEntryDTO>> getLedgerEntriesByPumpIdAndVoucherNumber(
            @PathVariable Long pumpId,
            @RequestParam String voucherNumber) {
        List<LedgerEntryDTO> entries = ledgerService.getLedgerEntriesByPumpIdAndVoucherNumber(pumpId, voucherNumber);
        return ResponseEntity.ok(entries);
    }

    // Amount Calculations
    @GetMapping("/total/debit/{accountId}")
    public ResponseEntity<BigDecimal> getTotalDebitByAccount(@PathVariable Long accountId) {
        BigDecimal total = ledgerService.getTotalDebitByAccount(accountId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/credit/{accountId}")
    public ResponseEntity<BigDecimal> getTotalCreditByAccount(@PathVariable Long accountId) {
        BigDecimal total = ledgerService.getTotalCreditByAccount(accountId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/debit/{accountId}/date-range")
    public ResponseEntity<BigDecimal> getTotalDebitByAccountAndDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal total = ledgerService.getTotalDebitByAccountAndDateRange(accountId, startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/credit/{accountId}/date-range")
    public ResponseEntity<BigDecimal> getTotalCreditByAccountAndDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal total = ledgerService.getTotalCreditByAccountAndDateRange(accountId, startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/current-balance/{accountId}")
    public ResponseEntity<BigDecimal> getCurrentBalance(@PathVariable Long accountId) {
        BigDecimal balance = ledgerService.getCurrentBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/balance-as-of-date/{accountId}")
    public ResponseEntity<BigDecimal> getBalanceAsOfDate(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        BigDecimal balance = ledgerService.getBalanceAsOfDate(accountId, asOfDate);
        return ResponseEntity.ok(balance);
    }

    // Latest Entry Operations
    @GetMapping("/entries/latest/account/{accountId}")
    public ResponseEntity<LedgerEntryDTO> getLatestEntryByAccount(@PathVariable Long accountId) {
        Optional<LedgerEntryDTO> entry = ledgerService.getLatestEntryByAccount(accountId);
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/entries/latest/pump/{pumpId}")
    public ResponseEntity<LedgerEntryDTO> getLatestEntryByPumpId(@PathVariable Long pumpId) {
        Optional<LedgerEntryDTO> entry = ledgerService.getLatestEntryByPumpId(pumpId);
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Count Operations
    @GetMapping("/count/account/{accountId}")
    public ResponseEntity<Long> countLedgerEntriesByAccount(@PathVariable Long accountId) {
        long count = ledgerService.countLedgerEntriesByAccount(accountId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/pump/{pumpId}")
    public ResponseEntity<Long> countLedgerEntriesByPumpId(@PathVariable Long pumpId) {
        long count = ledgerService.countLedgerEntriesByPumpId(pumpId);
        return ResponseEntity.ok(count);
    }

    // Search Operations
    @GetMapping("/entries/search/description")
    public ResponseEntity<List<LedgerEntryDTO>> searchLedgerEntriesByDescription(
            @RequestParam Long accountId,
            @RequestParam String description) {
        List<LedgerEntryDTO> entries = ledgerService.searchLedgerEntriesByDescription(accountId, description);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/entries/pump/{pumpId}/search/description")
    public ResponseEntity<List<LedgerEntryDTO>> searchLedgerEntriesByPumpIdAndDescription(
            @PathVariable Long pumpId,
            @RequestParam String description) {
        List<LedgerEntryDTO> entries = ledgerService.searchLedgerEntriesByPumpIdAndDescription(pumpId, description);
        return ResponseEntity.ok(entries);
    }

    // Analytics and Reports
    @GetMapping("/analytics/account/{accountId}")
    public ResponseEntity<Map<String, Object>> getLedgerAnalytics(@PathVariable Long accountId) {
        Map<String, Object> analytics = ledgerService.getLedgerAnalytics(accountId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getLedgerAnalyticsByPumpId(@PathVariable Long pumpId) {
        Map<String, Object> analytics = ledgerService.getLedgerAnalyticsByPumpId(pumpId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/summary/account/{accountId}")
    public ResponseEntity<Map<String, BigDecimal>> getLedgerSummaryByAccount(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> summary = ledgerService.getLedgerSummaryByAccount(accountId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/pump/{pumpId}")
    public ResponseEntity<Map<String, BigDecimal>> getLedgerSummaryByPumpId(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> summary = ledgerService.getLedgerSummaryByPumpId(pumpId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    // Bulk Operations
    @PostMapping("/entries/bulk/reconcile")
    public ResponseEntity<List<LedgerEntryDTO>> bulkReconcileEntries(
            @RequestBody List<Long> entryIds,
            @RequestParam Long reconciledBy) {
        try {
            List<LedgerEntryDTO> reconciledEntries = ledgerService.bulkReconcileEntries(entryIds, reconciledBy);
            return ResponseEntity.ok(reconciledEntries);
        } catch (Exception e) {
            log.error("Error bulk reconciling entries: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/entries/bulk/unreconcile")
    public ResponseEntity<List<LedgerEntryDTO>> bulkUnreconcileEntries(@RequestBody List<Long> entryIds) {
        try {
            List<LedgerEntryDTO> unreconciledEntries = ledgerService.bulkUnreconcileEntries(entryIds);
            return ResponseEntity.ok(unreconciledEntries);
        } catch (Exception e) {
            log.error("Error bulk unreconciling entries: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Ledger Reports
    @GetMapping("/customer/{customerAccountId}")
    public ResponseEntity<List<LedgerEntryDTO>> getCustomerLedger(
            @PathVariable Long customerAccountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LedgerEntryDTO> entries = ledgerService.getCustomerLedger(customerAccountId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/supplier/{supplierAccountId}")
    public ResponseEntity<List<LedgerEntryDTO>> getSupplierLedger(
            @PathVariable Long supplierAccountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LedgerEntryDTO> entries = ledgerService.getSupplierLedger(supplierAccountId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/cashier/{cashierAccountId}")
    public ResponseEntity<List<LedgerEntryDTO>> getCashierLedger(
            @PathVariable Long cashierAccountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LedgerEntryDTO> entries = ledgerService.getCashierLedger(cashierAccountId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/general/{pumpId}")
    public ResponseEntity<List<LedgerEntryDTO>> getGeneralLedger(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LedgerEntryDTO> entries = ledgerService.getGeneralLedger(pumpId, startDate, endDate);
        return ResponseEntity.ok(entries);
    }

    // Auto-generated Ledger Entries
    @PostMapping("/entries/from-voucher/{voucherId}")
    public ResponseEntity<LedgerEntryDTO> createLedgerEntryFromVoucher(@PathVariable Long voucherId) {
        try {
            LedgerEntryDTO createdEntry = ledgerService.createLedgerEntryFromVoucher(voucherId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (Exception e) {
            log.error("Error creating ledger entry from voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
