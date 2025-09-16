package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.VoucherEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VoucherEntryRepository extends JpaRepository<VoucherEntry, Long> {
    
    List<VoucherEntry> findByVoucherId(Long voucherId);
    
    List<VoucherEntry> findByAccountId(Long accountId);
    
    List<VoucherEntry> findByVoucherIdAndAccountId(Long voucherId, Long accountId);
    
    @Query("SELECT ve FROM VoucherEntry ve WHERE ve.voucher.pumpId = :pumpId")
    List<VoucherEntry> findByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT ve FROM VoucherEntry ve WHERE ve.account.id = :accountId AND ve.voucher.isPosted = true")
    List<VoucherEntry> findPostedEntriesByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT SUM(ve.debitAmount) FROM VoucherEntry ve WHERE ve.account.id = :accountId AND ve.voucher.isPosted = true")
    BigDecimal sumDebitAmountByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT SUM(ve.creditAmount) FROM VoucherEntry ve WHERE ve.account.id = :accountId AND ve.voucher.isPosted = true")
    BigDecimal sumCreditAmountByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT ve FROM VoucherEntry ve WHERE ve.voucher.pumpId = :pumpId AND ve.voucher.voucherType = :voucherType")
    List<VoucherEntry> findByPumpIdAndVoucherType(@Param("pumpId") Long pumpId, @Param("voucherType") String voucherType);
    
    @Query("SELECT ve FROM VoucherEntry ve WHERE ve.voucher.pumpId = :pumpId AND ve.entryType = :entryType")
    List<VoucherEntry> findByPumpIdAndEntryType(@Param("pumpId") Long pumpId, @Param("entryType") VoucherEntry.EntryType entryType);
    
    @Query("SELECT ve FROM VoucherEntry ve WHERE ve.account.id = :accountId AND ve.entryType = :entryType AND ve.voucher.isPosted = true")
    List<VoucherEntry> findPostedEntriesByAccountIdAndEntryType(@Param("accountId") Long accountId, @Param("entryType") VoucherEntry.EntryType entryType);
    
    @Query("SELECT ve FROM VoucherEntry ve WHERE ve.voucher.pumpId = :pumpId AND ve.partyName LIKE %:partyName%")
    List<VoucherEntry> findByPumpIdAndPartyNameContaining(@Param("pumpId") Long pumpId, @Param("partyName") String partyName);
    
    @Query("SELECT ve FROM VoucherEntry ve WHERE ve.voucher.pumpId = :pumpId AND ve.reference LIKE %:reference%")
    List<VoucherEntry> findByPumpIdAndReferenceContaining(@Param("pumpId") Long pumpId, @Param("reference") String reference);
    
    @Query("SELECT COUNT(ve) FROM VoucherEntry ve WHERE ve.voucher.pumpId = :pumpId")
    long countByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT COUNT(ve) FROM VoucherEntry ve WHERE ve.account.id = :accountId AND ve.voucher.isPosted = true")
    long countPostedEntriesByAccountId(@Param("accountId") Long accountId);
}
