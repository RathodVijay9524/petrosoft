package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.VoucherDTO;
import com.vijay.petrosoft.domain.Voucher;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VoucherService {
    
    // CRUD Operations
    VoucherDTO createVoucher(VoucherDTO voucherDTO);
    VoucherDTO updateVoucher(Long id, VoucherDTO voucherDTO);
    Optional<VoucherDTO> getVoucherById(Long id);
    Optional<VoucherDTO> getVoucherByNumber(String voucherNumber);
    List<VoucherDTO> getAllVouchers();
    List<VoucherDTO> getVouchersByPumpId(Long pumpId);
    void deleteVoucher(Long id);
    
    // Voucher Type Management
    List<VoucherDTO> getVouchersByType(Voucher.VoucherType voucherType);
    List<VoucherDTO> getVouchersByPumpIdAndType(Long pumpId, Voucher.VoucherType voucherType);
    
    // Voucher Status Management
    List<VoucherDTO> getVouchersByStatus(Voucher.VoucherStatus status);
    List<VoucherDTO> getVouchersByPumpIdAndStatus(Long pumpId, Voucher.VoucherStatus status);
    VoucherDTO approveVoucher(Long id, Long approvedBy);
    VoucherDTO postVoucher(Long id, Long postedBy);
    VoucherDTO cancelVoucher(Long id, Long cancelledBy, String reason);
    
    // Date Range Queries
    List<VoucherDTO> getVouchersByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate);
    List<VoucherDTO> getVouchersByTypeAndDateRange(Long pumpId, Voucher.VoucherType voucherType, LocalDate startDate, LocalDate endDate);
    
    // Posted Voucher Management
    List<VoucherDTO> getPostedVouchers(Long pumpId);
    List<VoucherDTO> getPostedVouchersByType(Long pumpId, Voucher.VoucherType voucherType);
    List<VoucherDTO> getUnpostedVouchers(Long pumpId);
    
    // Cancelled Voucher Management
    List<VoucherDTO> getCancelledVouchers(Long pumpId);
    VoucherDTO uncancelVoucher(Long id);
    
    // User Voucher Management
    List<VoucherDTO> getVouchersByUser(Long pumpId, Long userId);
    
    // Party Management
    List<VoucherDTO> getVouchersByPartyName(Long pumpId, String partyName);
    
    // Payment Mode Management
    List<VoucherDTO> getVouchersByPaymentMode(Long pumpId, String paymentMode);
    List<VoucherDTO> getVouchersByPaymentModeAndDateRange(Long pumpId, String paymentMode, LocalDate startDate, LocalDate endDate);
    
    // Cheque Management
    List<VoucherDTO> getVouchersByChequeNumber(Long pumpId, String chequeNumber);
    List<VoucherDTO> getVouchersByChequeDate(Long pumpId, LocalDate chequeDate);
    
    // Reconciliation Management
    List<VoucherDTO> getUnreconciledVouchers(Long pumpId);
    List<VoucherDTO> getReconciledVouchers(Long pumpId);
    VoucherDTO reconcileVoucher(Long id, Long reconciledBy);
    VoucherDTO unreconcileVoucher(Long id);
    
    // Voucher Number Generation
    String generateVoucherNumber(Voucher.VoucherType voucherType, Long pumpId);
    boolean validateVoucherNumber(String voucherNumber);
    
    // Double Entry Validation
    boolean validateDoubleEntry(VoucherDTO voucherDTO);
    boolean validateVoucherBalances(VoucherDTO voucherDTO);
    
    // Amount Calculations
    java.math.BigDecimal calculateTotalDebit(VoucherDTO voucherDTO);
    java.math.BigDecimal calculateTotalCredit(VoucherDTO voucherDTO);
    java.math.BigDecimal getTotalAmountByType(Long pumpId, Voucher.VoucherType voucherType, LocalDate startDate, LocalDate endDate);
    
    // Search and Filter
    List<VoucherDTO> searchVouchersByNumber(Long pumpId, String voucherNumber);
    List<VoucherDTO> searchVouchersByReference(Long pumpId, String reference);
    
    // Analytics and Reports
    Map<String, Object> getVoucherAnalytics(Long pumpId);
    Map<String, Long> getVoucherCountByType(Long pumpId);
    Map<String, java.math.BigDecimal> getVoucherAmountByType(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    // Bulk Operations
    List<VoucherDTO> bulkPostVouchers(List<Long> voucherIds, Long postedBy);
    List<VoucherDTO> bulkCancelVouchers(List<Long> voucherIds, Long cancelledBy, String reason);
    
    // Specific Voucher Types
    VoucherDTO createCustomerReceipt(VoucherDTO voucherDTO);
    VoucherDTO createPaymentVoucher(VoucherDTO voucherDTO);
    VoucherDTO createMiscellaneousReceipt(VoucherDTO voucherDTO);
    VoucherDTO createJournalVoucher(VoucherDTO voucherDTO);
    VoucherDTO createCashDeposit(VoucherDTO voucherDTO);
    VoucherDTO createCashWithdrawal(VoucherDTO voucherDTO);
    VoucherDTO createContraVoucher(VoucherDTO voucherDTO);
    VoucherDTO createChequeReturn(VoucherDTO voucherDTO);
    
    // Voucher Templates
    List<VoucherDTO> getVoucherTemplates(Voucher.VoucherType voucherType);
    VoucherDTO createVoucherFromTemplate(Long templateId, Long pumpId, Long userId);
}
