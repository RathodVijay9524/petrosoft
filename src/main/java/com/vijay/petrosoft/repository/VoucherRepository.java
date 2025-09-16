package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    
    Optional<Voucher> findByVoucherNumber(String voucherNumber);
    
    List<Voucher> findByPumpId(Long pumpId);
    
    List<Voucher> findByPumpIdAndVoucherType(Long pumpId, Voucher.VoucherType voucherType);
    
    List<Voucher> findByPumpIdAndStatus(Long pumpId, Voucher.VoucherStatus status);
    
    List<Voucher> findByPumpIdAndVoucherDateBetween(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    List<Voucher> findByPumpIdAndVoucherTypeAndVoucherDateBetween(Long pumpId, Voucher.VoucherType voucherType, LocalDate startDate, LocalDate endDate);
    
    List<Voucher> findByPumpIdAndIsPostedTrue(Long pumpId);
    
    List<Voucher> findByPumpIdAndIsCancelledTrue(Long pumpId);
    
    List<Voucher> findByPumpIdAndUserId(Long pumpId, Long userId);
    
    List<Voucher> findByPumpIdAndPartyNameContaining(Long pumpId, String partyName);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.isPosted = true ORDER BY v.voucherDate DESC, v.id DESC")
    List<Voucher> findPostedVouchersByPumpIdOrderByDateDesc(@Param("pumpId") Long pumpId);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.voucherType = :voucherType AND v.isPosted = true ORDER BY v.voucherDate DESC, v.id DESC")
    List<Voucher> findPostedVouchersByPumpIdAndTypeOrderByDateDesc(@Param("pumpId") Long pumpId, @Param("voucherType") Voucher.VoucherType voucherType);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.voucherDate BETWEEN :startDate AND :endDate AND v.isPosted = true ORDER BY v.voucherDate DESC, v.id DESC")
    List<Voucher> findPostedVouchersByPumpIdAndDateRange(@Param("pumpId") Long pumpId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.voucherType = :voucherType AND v.voucherDate BETWEEN :startDate AND :endDate AND v.isPosted = true ORDER BY v.voucherDate DESC, v.id DESC")
    List<Voucher> findPostedVouchersByPumpIdAndTypeAndDateRange(@Param("pumpId") Long pumpId, @Param("voucherType") Voucher.VoucherType voucherType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.isReconciled = false AND v.isPosted = true")
    List<Voucher> findUnreconciledVouchersByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.chequeNumber = :chequeNumber AND v.isPosted = true")
    List<Voucher> findVouchersByPumpIdAndChequeNumber(@Param("pumpId") Long pumpId, @Param("chequeNumber") String chequeNumber);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.paymentMode = :paymentMode AND v.voucherDate BETWEEN :startDate AND :endDate AND v.isPosted = true")
    List<Voucher> findVouchersByPumpIdAndPaymentModeAndDateRange(@Param("pumpId") Long pumpId, @Param("paymentMode") String paymentMode, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(v) FROM Voucher v WHERE v.pumpId = :pumpId AND v.isPosted = true")
    long countPostedVouchersByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT COUNT(v) FROM Voucher v WHERE v.pumpId = :pumpId AND v.voucherType = :voucherType AND v.isPosted = true")
    long countPostedVouchersByPumpIdAndType(@Param("pumpId") Long pumpId, @Param("voucherType") Voucher.VoucherType voucherType);
    
    @Query("SELECT SUM(v.totalAmount) FROM Voucher v WHERE v.pumpId = :pumpId AND v.voucherType = :voucherType AND v.voucherDate BETWEEN :startDate AND :endDate AND v.isPosted = true")
    Double sumTotalAmountByPumpIdAndTypeAndDateRange(@Param("pumpId") Long pumpId, @Param("voucherType") Voucher.VoucherType voucherType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.voucherNumber LIKE %:voucherNumber% ORDER BY v.voucherNumber")
    List<Voucher> findByPumpIdAndVoucherNumberContaining(@Param("pumpId") Long pumpId, @Param("voucherNumber") String voucherNumber);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.reference LIKE %:reference% ORDER BY v.voucherDate DESC")
    List<Voucher> findByPumpIdAndReferenceContaining(@Param("pumpId") Long pumpId, @Param("reference") String reference);
    
    boolean existsByVoucherNumber(String voucherNumber);
    
    boolean existsByVoucherNumberAndPumpId(String voucherNumber, Long pumpId);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND DATE(v.voucherDate) = :reportDate AND v.isPosted = true ORDER BY v.voucherDate DESC, v.id DESC")
    List<Voucher> findPostedVouchersByPumpIdAndDateOrderByDateDesc(@Param("pumpId") Long pumpId, @Param("reportDate") LocalDate reportDate);
    
    @Query("SELECT v FROM Voucher v WHERE v.pumpId = :pumpId AND v.voucherDate BETWEEN :fromDate AND :toDate AND v.isPosted = true ORDER BY v.voucherDate DESC, v.id DESC")
    List<Voucher> findPostedVouchersByPumpIdAndDateRangeOrderByDateDesc(@Param("pumpId") Long pumpId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
