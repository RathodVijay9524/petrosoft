package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    
    List<LedgerEntry> findByAccountId(Long accountId);
    
    List<LedgerEntry> findByPumpId(Long pumpId);
    
    List<LedgerEntry> findByAccountIdAndTransactionDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);
    
    List<LedgerEntry> findByPumpIdAndTransactionDateBetween(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    List<LedgerEntry> findByAccountIdOrderByTransactionDateAsc(Long accountId);
    
    List<LedgerEntry> findByPumpIdOrderByTransactionDateDesc(Long pumpId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.isReconciled = false ORDER BY le.transactionDate ASC")
    List<LedgerEntry> findUnreconciledEntriesByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.isReconciled = false ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findUnreconciledEntriesByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.entryType = :entryType ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByAccountIdAndEntryType(@Param("accountId") Long accountId, @Param("entryType") LedgerEntry.EntryType entryType);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.entryType = :entryType ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByPumpIdAndEntryType(@Param("pumpId") Long pumpId, @Param("entryType") LedgerEntry.EntryType entryType);
    
    @Query("SELECT SUM(le.debitAmount) FROM LedgerEntry le WHERE le.account.id = :accountId")
    BigDecimal sumDebitAmountByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT SUM(le.creditAmount) FROM LedgerEntry le WHERE le.account.id = :accountId")
    BigDecimal sumCreditAmountByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT SUM(le.debitAmount) FROM LedgerEntry le WHERE le.account.id = :accountId AND le.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumDebitAmountByAccountIdAndDateRange(@Param("accountId") Long accountId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(le.creditAmount) FROM LedgerEntry le WHERE le.account.id = :accountId AND le.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCreditAmountByAccountIdAndDateRange(@Param("accountId") Long accountId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.partyName LIKE %:partyName% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByAccountIdAndPartyNameContaining(@Param("accountId") Long accountId, @Param("partyName") String partyName);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.partyName LIKE %:partyName% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByPumpIdAndPartyNameContaining(@Param("pumpId") Long pumpId, @Param("partyName") String partyName);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.chequeNumber = :chequeNumber")
    List<LedgerEntry> findByAccountIdAndChequeNumber(@Param("accountId") Long accountId, @Param("chequeNumber") String chequeNumber);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.chequeNumber = :chequeNumber")
    List<LedgerEntry> findByPumpIdAndChequeNumber(@Param("pumpId") Long pumpId, @Param("chequeNumber") String chequeNumber);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.reference LIKE %:reference% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByAccountIdAndReferenceContaining(@Param("accountId") Long accountId, @Param("reference") String reference);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.reference LIKE %:reference% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByPumpIdAndReferenceContaining(@Param("pumpId") Long pumpId, @Param("reference") String reference);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.voucherNumber = :voucherNumber")
    List<LedgerEntry> findByAccountIdAndVoucherNumber(@Param("accountId") Long accountId, @Param("voucherNumber") String voucherNumber);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.voucherNumber = :voucherNumber")
    List<LedgerEntry> findByPumpIdAndVoucherNumber(@Param("pumpId") Long pumpId, @Param("voucherNumber") String voucherNumber);
    
    @Query("SELECT COUNT(le) FROM LedgerEntry le WHERE le.account.id = :accountId")
    long countByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT COUNT(le) FROM LedgerEntry le WHERE le.pumpId = :pumpId")
    long countByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId ORDER BY le.transactionDate DESC, le.id DESC LIMIT 1")
    LedgerEntry findLatestEntryByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId ORDER BY le.transactionDate DESC, le.id DESC LIMIT 1")
    LedgerEntry findLatestEntryByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.transactionDate <= :asOfDate ORDER BY le.transactionDate ASC")
    List<LedgerEntry> findByAccountIdAndTransactionDateLessThanEqualOrderByTransactionDateAsc(@Param("accountId") Long accountId, @Param("asOfDate") LocalDate asOfDate);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.isReconciled = false ORDER BY le.transactionDate ASC")
    List<LedgerEntry> findByAccountIdAndIsReconciledFalse(@Param("accountId") Long accountId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.isReconciled = false ORDER BY le.transactionDate ASC")
    List<LedgerEntry> findByPumpIdAndIsReconciledFalse(@Param("pumpId") Long pumpId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.isReconciled = true ORDER BY le.transactionDate ASC")
    List<LedgerEntry> findByAccountIdAndIsReconciledTrue(@Param("accountId") Long accountId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.voucher.voucherNumber LIKE %:voucherNumber% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByAccountIdAndVoucherVoucherNumberContaining(@Param("accountId") Long accountId, @Param("voucherNumber") String voucherNumber);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.voucher.voucherNumber LIKE %:voucherNumber% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByPumpIdAndVoucherVoucherNumberContaining(@Param("pumpId") Long pumpId, @Param("voucherNumber") String voucherNumber);
    
    @Query("SELECT SUM(le.amount) FROM LedgerEntry le WHERE le.account.id = :accountId AND le.entryType = :entryType")
    Double sumAmountByAccountIdAndEntryType(@Param("accountId") Long accountId, @Param("entryType") LedgerEntry.EntryType entryType);
    
    @Query("SELECT SUM(le.amount) FROM LedgerEntry le WHERE le.account.id = :accountId AND le.entryType = :entryType AND le.transactionDate BETWEEN :startDate AND :endDate")
    Double sumAmountByAccountIdAndEntryTypeAndDateRange(@Param("accountId") Long accountId, @Param("entryType") LedgerEntry.EntryType entryType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId ORDER BY le.transactionDate DESC, le.id DESC LIMIT 1")
    LedgerEntry findTopByAccountIdOrderByTransactionDateDesc(@Param("accountId") Long accountId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId ORDER BY le.transactionDate DESC, le.id DESC LIMIT 1")
    LedgerEntry findTopByPumpIdOrderByTransactionDateDesc(@Param("pumpId") Long pumpId);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.account.id = :accountId AND le.narration LIKE %:description% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByAccountIdAndNarrationContaining(@Param("accountId") Long accountId, @Param("description") String description);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.pumpId = :pumpId AND le.narration LIKE %:description% ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByPumpIdAndNarrationContaining(@Param("pumpId") Long pumpId, @Param("description") String description);
}