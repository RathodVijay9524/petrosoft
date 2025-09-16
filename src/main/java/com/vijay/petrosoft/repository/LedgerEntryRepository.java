package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    
    // Basic queries
    List<LedgerEntry> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    List<LedgerEntry> findByPumpId(Long pumpId);
    List<LedgerEntry> findByPartyName(String partyName);
    
    // Account-based queries
    List<LedgerEntry> findByAccountId(Long accountId);
    
    List<LedgerEntry> findByAccountIdAndTransactionDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);
    
    List<LedgerEntry> findByAccountIdOrderByTransactionDateAsc(Long accountId);
    
    List<LedgerEntry> findByAccountIdAndEntryType(Long accountId, LedgerEntry.EntryType entryType);
    
    List<LedgerEntry> findByAccountIdAndTransactionDateLessThanEqualOrderByTransactionDateAsc(Long accountId, LocalDate date);
    
    List<LedgerEntry> findByAccountIdAndIsReconciledFalse(Long accountId);
    
    List<LedgerEntry> findByAccountIdAndIsReconciledTrue(Long accountId);
    
    List<LedgerEntry> findByAccountIdAndPartyNameContaining(Long accountId, String partyName);
    
    List<LedgerEntry> findByAccountIdAndReferenceContaining(Long accountId, String reference);
    
    List<LedgerEntry> findByAccountIdAndVoucherVoucherNumberContaining(Long accountId, String voucherNumber);
    
    List<LedgerEntry> findByAccountIdAndNarrationContaining(Long accountId, String narration);
    
    Optional<LedgerEntry> findTopByAccountIdOrderByTransactionDateDesc(Long accountId);
    
    long countByAccountId(Long accountId);
    
    // Pump-based queries
    List<LedgerEntry> findByPumpIdAndTransactionDateBetween(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    List<LedgerEntry> findByPumpIdAndEntryType(Long pumpId, LedgerEntry.EntryType entryType);
    
    List<LedgerEntry> findByPumpIdAndIsReconciledFalse(Long pumpId);
    
    List<LedgerEntry> findByPumpIdAndIsReconciledTrue(Long pumpId);
    
    List<LedgerEntry> findByPumpIdAndPartyNameContaining(Long pumpId, String partyName);
    
    List<LedgerEntry> findByPumpIdAndReferenceContaining(Long pumpId, String reference);
    
    List<LedgerEntry> findByPumpIdAndVoucherVoucherNumberContaining(Long pumpId, String voucherNumber);
    
    List<LedgerEntry> findByPumpIdAndNarrationContaining(Long pumpId, String narration);
    
    Optional<LedgerEntry> findTopByPumpIdOrderByTransactionDateDesc(Long pumpId);
    
    long countByPumpId(Long pumpId);
    
    // Aggregation queries
    @Query("SELECT COALESCE(SUM(l.debitAmount), 0) FROM LedgerEntry l WHERE l.account.id = :accountId AND l.entryType = :entryType")
    BigDecimal sumAmountByAccountIdAndEntryType(@Param("accountId") Long accountId, @Param("entryType") LedgerEntry.EntryType entryType);
    
    @Query("SELECT COALESCE(SUM(l.debitAmount), 0) FROM LedgerEntry l WHERE l.account.id = :accountId AND l.entryType = :entryType AND l.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByAccountIdAndEntryTypeAndDateRange(@Param("accountId") Long accountId, @Param("entryType") LedgerEntry.EntryType entryType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(l.creditAmount), 0) FROM LedgerEntry l WHERE l.account.id = :accountId AND l.entryType = :entryType")
    BigDecimal sumCreditAmountByAccountIdAndEntryType(@Param("accountId") Long accountId, @Param("entryType") LedgerEntry.EntryType entryType);
    
    @Query("SELECT COALESCE(SUM(l.creditAmount), 0) FROM LedgerEntry l WHERE l.account.id = :accountId AND l.entryType = :entryType AND l.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCreditAmountByAccountIdAndEntryTypeAndDateRange(@Param("accountId") Long accountId, @Param("entryType") LedgerEntry.EntryType entryType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}