package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Account;
import com.vijay.petrosoft.domain.LedgerEntry;
import com.vijay.petrosoft.domain.Voucher;
import com.vijay.petrosoft.domain.VoucherEntry;
import com.vijay.petrosoft.dto.LedgerEntryDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.repository.AccountRepository;
import com.vijay.petrosoft.repository.LedgerEntryRepository;
import com.vijay.petrosoft.repository.VoucherRepository;
import com.vijay.petrosoft.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountRepository accountRepository;
    private final VoucherRepository voucherRepository;

    @Override
    public LedgerEntryDTO createLedgerEntry(LedgerEntryDTO ledgerEntryDTO) {
        Account account = accountRepository.findById(ledgerEntryDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + ledgerEntryDTO.getAccountId()));

        LedgerEntry ledgerEntry = LedgerEntry.builder()
                .account(account)
                .transactionDate(ledgerEntryDTO.getTransactionDate())
                .entryType(ledgerEntryDTO.getEntryType())
                .amount(ledgerEntryDTO.getAmount())
                .narration(ledgerEntryDTO.getNarration())
                .reference(ledgerEntryDTO.getReference())
                .partyName(ledgerEntryDTO.getPartyName())
                .partyId(ledgerEntryDTO.getPartyId())
                .runningBalance(ledgerEntryDTO.getRunningBalance())
                .isReconciled(ledgerEntryDTO.isReconciled())
                .reconciledAt(ledgerEntryDTO.getReconciledAt())
                .pumpId(ledgerEntryDTO.getPumpId())
                .build();

        // Set voucher if provided
        if (ledgerEntryDTO.getVoucherId() != null) {
            Voucher voucher = voucherRepository.findById(ledgerEntryDTO.getVoucherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + ledgerEntryDTO.getVoucherId()));
            ledgerEntry.setVoucher(voucher);
        }

        // Calculate running balance
        BigDecimal newRunningBalance = calculateRunningBalance(account.getId(), ledgerEntryDTO.getTransactionDate());
        ledgerEntry.setRunningBalance(newRunningBalance);

        LedgerEntry savedEntry = ledgerEntryRepository.save(ledgerEntry);
        log.info("Ledger entry created for account ID: {}", account.getId());
        
        return convertToDTO(savedEntry);
    }

    @Override
    public LedgerEntryDTO updateLedgerEntry(Long id, LedgerEntryDTO ledgerEntryDTO) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ledger entry not found with id: " + id));

        if (ledgerEntry.isReconciled()) {
            throw new IllegalStateException("Cannot update reconciled ledger entry");
        }

        ledgerEntry.setTransactionDate(ledgerEntryDTO.getTransactionDate());
        ledgerEntry.setEntryType(ledgerEntryDTO.getEntryType());
        ledgerEntry.setAmount(ledgerEntryDTO.getAmount());
        ledgerEntry.setNarration(ledgerEntryDTO.getNarration());
        ledgerEntry.setReference(ledgerEntryDTO.getReference());
        ledgerEntry.setPartyName(ledgerEntryDTO.getPartyName());
        ledgerEntry.setPartyId(ledgerEntryDTO.getPartyId());

        // Recalculate running balance
        BigDecimal newRunningBalance = calculateRunningBalance(ledgerEntry.getAccount().getId(), ledgerEntryDTO.getTransactionDate());
        ledgerEntry.setRunningBalance(newRunningBalance);

        LedgerEntry updatedEntry = ledgerEntryRepository.save(ledgerEntry);
        log.info("Ledger entry updated for ID: {}", id);
        
        return convertToDTO(updatedEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LedgerEntryDTO> getLedgerEntryById(Long id) {
        return ledgerEntryRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getAllLedgerEntries() {
        return ledgerEntryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByPumpId(Long pumpId) {
        return ledgerEntryRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByAccount(Long accountId) {
        return ledgerEntryRepository.findByAccountId(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByAccountAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate) {
        return ledgerEntryRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByAccountOrderByDate(Long accountId) {
        return ledgerEntryRepository.findByAccountIdOrderByTransactionDateAsc(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate) {
        return ledgerEntryRepository.findByPumpIdAndTransactionDateBetween(pumpId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByAccountAndType(Long accountId, LedgerEntry.EntryType entryType) {
        return ledgerEntryRepository.findByAccountIdAndEntryType(accountId, entryType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndType(Long pumpId, LedgerEntry.EntryType entryType) {
        return ledgerEntryRepository.findByPumpIdAndEntryType(pumpId, entryType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateRunningBalance(Long accountId, LocalDate asOfDate) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByAccountIdAndTransactionDateLessThanEqualOrderByTransactionDateAsc(accountId, asOfDate);
        
        BigDecimal runningBalance = BigDecimal.ZERO;
        Account account = accountRepository.findById(accountId).orElse(null);
        
        if (account != null) {
            runningBalance = account.getOpeningBalance();
        }
        
        for (LedgerEntry entry : entries) {
            if (entry.getEntryType() == LedgerEntry.EntryType.DEBIT) {
                runningBalance = runningBalance.add(entry.getAmount());
            } else {
                runningBalance = runningBalance.subtract(entry.getAmount());
            }
        }
        
        return runningBalance;
    }

    @Override
    public LedgerEntryDTO updateRunningBalance(Long id, BigDecimal runningBalance) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ledger entry not found with id: " + id));

        ledgerEntry.setRunningBalance(runningBalance);
        LedgerEntry updatedEntry = ledgerEntryRepository.save(ledgerEntry);
        return convertToDTO(updatedEntry);
    }

    @Override
    public List<LedgerEntryDTO> recalculateRunningBalances(Long accountId) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByAccountIdOrderByTransactionDateAsc(accountId);
        List<LedgerEntryDTO> updatedEntries = new ArrayList<>();
        
        BigDecimal runningBalance = BigDecimal.ZERO;
        Account account = accountRepository.findById(accountId).orElse(null);
        
        if (account != null) {
            runningBalance = account.getOpeningBalance();
        }
        
        for (LedgerEntry entry : entries) {
            if (entry.getEntryType() == LedgerEntry.EntryType.DEBIT) {
                runningBalance = runningBalance.add(entry.getAmount());
            } else {
                runningBalance = runningBalance.subtract(entry.getAmount());
            }
            
            entry.setRunningBalance(runningBalance);
            LedgerEntry savedEntry = ledgerEntryRepository.save(entry);
            updatedEntries.add(convertToDTO(savedEntry));
        }
        
        log.info("Recalculated running balances for account ID: {}", accountId);
        return updatedEntries;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getUnreconciledEntries(Long accountId) {
        return ledgerEntryRepository.findByAccountIdAndIsReconciledFalse(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getUnreconciledEntriesByPumpId(Long pumpId) {
        return ledgerEntryRepository.findByPumpIdAndIsReconciledFalse(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getReconciledEntries(Long accountId) {
        return ledgerEntryRepository.findByAccountIdAndIsReconciledTrue(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LedgerEntryDTO reconcileEntry(Long id, Long reconciledBy) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ledger entry not found with id: " + id));

        ledgerEntry.setReconciled(true);
        ledgerEntry.setReconciledAt(LocalDateTime.now());
        ledgerEntry.setReconciledBy(reconciledBy);

        LedgerEntry updatedEntry = ledgerEntryRepository.save(ledgerEntry);
        log.info("Ledger entry reconciled for ID: {}", id);
        
        return convertToDTO(updatedEntry);
    }

    @Override
    public LedgerEntryDTO unreconcileEntry(Long id) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ledger entry not found with id: " + id));

        ledgerEntry.setReconciled(false);
        ledgerEntry.setReconciledAt(null);
        ledgerEntry.setReconciledBy(null);

        LedgerEntry updatedEntry = ledgerEntryRepository.save(ledgerEntry);
        log.info("Ledger entry unreconciled for ID: {}", id);
        
        return convertToDTO(updatedEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByPartyName(Long accountId, String partyName) {
        return ledgerEntryRepository.findByAccountIdAndPartyNameContaining(accountId, partyName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndPartyName(Long pumpId, String partyName) {
        return ledgerEntryRepository.findByPumpIdAndPartyNameContaining(pumpId, partyName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByReference(Long accountId, String reference) {
        return ledgerEntryRepository.findByAccountIdAndReferenceContaining(accountId, reference).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndReference(Long pumpId, String reference) {
        return ledgerEntryRepository.findByPumpIdAndReferenceContaining(pumpId, reference).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByChequeNumber(Long accountId, String chequeNumber) {
        return ledgerEntryRepository.findByAccountIdAndReferenceContaining(accountId, chequeNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndChequeNumber(Long pumpId, String chequeNumber) {
        return ledgerEntryRepository.findByPumpIdAndReferenceContaining(pumpId, chequeNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByChequeDate(Long accountId, LocalDate chequeDate) {
        // This would need a custom query in the repository
        return getLedgerEntriesByAccount(accountId).stream()
                .filter(entry -> entry.getReference() != null && entry.getReference().contains(chequeDate.toString()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByVoucherNumber(Long accountId, String voucherNumber) {
        return ledgerEntryRepository.findByAccountIdAndVoucherVoucherNumberContaining(accountId, voucherNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getLedgerEntriesByPumpIdAndVoucherNumber(Long pumpId, String voucherNumber) {
        return ledgerEntryRepository.findByPumpIdAndVoucherVoucherNumberContaining(pumpId, voucherNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDebitByAccount(Long accountId) {
        BigDecimal total = ledgerEntryRepository.sumAmountByAccountIdAndEntryType(accountId, LedgerEntry.EntryType.DEBIT);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCreditByAccount(Long accountId) {
        BigDecimal total = ledgerEntryRepository.sumCreditAmountByAccountIdAndEntryType(accountId, LedgerEntry.EntryType.CREDIT);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDebitByAccountAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = ledgerEntryRepository.sumAmountByAccountIdAndEntryTypeAndDateRange(accountId, LedgerEntry.EntryType.DEBIT, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCreditByAccountAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = ledgerEntryRepository.sumCreditAmountByAccountIdAndEntryTypeAndDateRange(accountId, LedgerEntry.EntryType.CREDIT, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance(Long accountId) {
        return calculateRunningBalance(accountId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalanceAsOfDate(Long accountId, LocalDate asOfDate) {
        return calculateRunningBalance(accountId, asOfDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LedgerEntryDTO> getLatestEntryByAccount(Long accountId) {
        Optional<LedgerEntry> entry = ledgerEntryRepository.findTopByAccountIdOrderByTransactionDateDesc(accountId);
        return entry.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LedgerEntryDTO> getLatestEntryByPumpId(Long pumpId) {
        Optional<LedgerEntry> entry = ledgerEntryRepository.findTopByPumpIdOrderByTransactionDateDesc(pumpId);
        return entry.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long countLedgerEntriesByAccount(Long accountId) {
        return ledgerEntryRepository.countByAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countLedgerEntriesByPumpId(Long pumpId) {
        return ledgerEntryRepository.countByPumpId(pumpId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> searchLedgerEntriesByDescription(Long accountId, String description) {
        return ledgerEntryRepository.findByAccountIdAndNarrationContaining(accountId, description).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> searchLedgerEntriesByPumpIdAndDescription(Long pumpId, String description) {
        return ledgerEntryRepository.findByPumpIdAndNarrationContaining(pumpId, description).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLedgerAnalytics(Long accountId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEntries", countLedgerEntriesByAccount(accountId));
        analytics.put("totalDebit", getTotalDebitByAccount(accountId));
        analytics.put("totalCredit", getTotalCreditByAccount(accountId));
        analytics.put("currentBalance", getCurrentBalance(accountId));
        analytics.put("unreconciledEntries", getUnreconciledEntries(accountId).size());
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLedgerAnalyticsByPumpId(Long pumpId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEntries", countLedgerEntriesByPumpId(pumpId));
        analytics.put("unreconciledEntries", getUnreconciledEntriesByPumpId(pumpId).size());
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getLedgerSummaryByAccount(Long accountId, LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalDebit", getTotalDebitByAccountAndDateRange(accountId, startDate, endDate));
        summary.put("totalCredit", getTotalCreditByAccountAndDateRange(accountId, startDate, endDate));
        summary.put("openingBalance", getBalanceAsOfDate(accountId, startDate.minusDays(1)));
        summary.put("closingBalance", getBalanceAsOfDate(accountId, endDate));
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getLedgerSummaryByPumpId(Long pumpId, LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> summary = new HashMap<>();
        List<LedgerEntryDTO> entries = getLedgerEntriesByDateRange(pumpId, startDate, endDate);
        
        BigDecimal totalDebit = entries.stream()
                .filter(e -> e.getEntryType() == LedgerEntry.EntryType.DEBIT)
                .map(LedgerEntryDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCredit = entries.stream()
                .filter(e -> e.getEntryType() == LedgerEntry.EntryType.CREDIT)
                .map(LedgerEntryDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        summary.put("totalDebit", totalDebit);
        summary.put("totalCredit", totalCredit);
        summary.put("netBalance", totalDebit.subtract(totalCredit));
        
        return summary;
    }

    @Override
    public List<LedgerEntryDTO> bulkReconcileEntries(List<Long> entryIds, Long reconciledBy) {
        List<LedgerEntryDTO> reconciledEntries = new ArrayList<>();
        
        for (Long entryId : entryIds) {
            try {
                LedgerEntryDTO reconciledEntry = reconcileEntry(entryId, reconciledBy);
                reconciledEntries.add(reconciledEntry);
            } catch (Exception e) {
                log.error("Failed to reconcile ledger entry ID: {}", entryId, e);
            }
        }
        
        return reconciledEntries;
    }

    @Override
    public List<LedgerEntryDTO> bulkUnreconcileEntries(List<Long> entryIds) {
        List<LedgerEntryDTO> unreconciledEntries = new ArrayList<>();
        
        for (Long entryId : entryIds) {
            try {
                LedgerEntryDTO unreconciledEntry = unreconcileEntry(entryId);
                unreconciledEntries.add(unreconciledEntry);
            } catch (Exception e) {
                log.error("Failed to unreconcile ledger entry ID: {}", entryId, e);
            }
        }
        
        return unreconciledEntries;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getCustomerLedger(Long customerAccountId, LocalDate startDate, LocalDate endDate) {
        return getLedgerEntriesByAccountAndDateRange(customerAccountId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getSupplierLedger(Long supplierAccountId, LocalDate startDate, LocalDate endDate) {
        return getLedgerEntriesByAccountAndDateRange(supplierAccountId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getCashierLedger(Long cashierAccountId, LocalDate startDate, LocalDate endDate) {
        return getLedgerEntriesByAccountAndDateRange(cashierAccountId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryDTO> getGeneralLedger(Long pumpId, LocalDate startDate, LocalDate endDate) {
        return getLedgerEntriesByDateRange(pumpId, startDate, endDate);
    }

    @Override
    public LedgerEntryDTO createLedgerEntryFromVoucher(Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + voucherId));

        if (!voucher.isPosted()) {
            throw new IllegalStateException("Cannot create ledger entries from unposted voucher");
        }

        List<LedgerEntryDTO> createdEntries = new ArrayList<>();
        
        for (VoucherEntry voucherEntry : voucher.getVoucherEntries()) {
            LedgerEntryDTO ledgerEntryDTO = LedgerEntryDTO.builder()
                    .accountId(voucherEntry.getAccount().getId())
                    .transactionDate(voucher.getVoucherDate())
                    .entryType(LedgerEntry.EntryType.valueOf(voucherEntry.getEntryType().name()))
                    .amount(voucherEntry.getAmount())
                    .narration(voucherEntry.getNarration())
                    .reference(voucherEntry.getReference())
                    .partyName(voucherEntry.getPartyName())
                    .partyId(voucherEntry.getPartyId())
                    .pumpId(voucher.getPumpId())
                    .voucherId(voucher.getId())
                    .build();

            LedgerEntryDTO createdEntry = createLedgerEntry(ledgerEntryDTO);
            createdEntries.add(createdEntry);
        }
        
        log.info("Created {} ledger entries from voucher ID: {}", createdEntries.size(), voucherId);
        return createdEntries.isEmpty() ? null : createdEntries.get(0);
    }

    public void deleteLedgerEntry(Long id) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ledger entry not found with id: " + id));

        if (ledgerEntry.isReconciled()) {
            throw new IllegalStateException("Cannot delete reconciled ledger entry");
        }

        ledgerEntryRepository.deleteById(id);
        log.info("Ledger entry deleted for ID: {}", id);
    }

    private LedgerEntryDTO convertToDTO(LedgerEntry ledgerEntry) {
        return LedgerEntryDTO.builder()
                .id(ledgerEntry.getId())
                .accountId(ledgerEntry.getAccount().getId())
                .transactionDate(ledgerEntry.getTransactionDate())
                .entryType(ledgerEntry.getEntryType())
                .amount(ledgerEntry.getAmount())
                .narration(ledgerEntry.getNarration())
                .reference(ledgerEntry.getReference())
                .partyName(ledgerEntry.getPartyName())
                .partyId(ledgerEntry.getPartyId())
                .runningBalance(ledgerEntry.getRunningBalance())
                .isReconciled(ledgerEntry.isReconciled())
                .reconciledAt(ledgerEntry.getReconciledAt())
                .pumpId(ledgerEntry.getPumpId())
                .voucherId(ledgerEntry.getVoucher() != null ? ledgerEntry.getVoucher().getId() : null)
                .build();
    }
}
