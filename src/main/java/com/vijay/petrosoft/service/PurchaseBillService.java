package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.PurchaseBill;
import com.vijay.petrosoft.dto.PurchaseBillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PurchaseBillService {
    
    // Purchase Bill CRUD Operations
    PurchaseBillDTO createPurchaseBill(PurchaseBillDTO purchaseBillDTO);
    PurchaseBillDTO getPurchaseBillById(Long id);
    PurchaseBillDTO getPurchaseBillByBillNumber(String billNumber);
    PurchaseBillDTO updatePurchaseBill(Long id, PurchaseBillDTO purchaseBillDTO);
    void deletePurchaseBill(Long id);
    
    // Purchase Bill Status Management
    PurchaseBillDTO submitPurchaseBill(Long id);
    PurchaseBillDTO approvePurchaseBill(Long id, Long approvedBy);
    PurchaseBillDTO receivePurchaseBill(Long id, Long receivedBy);
    PurchaseBillDTO invoicePurchaseBill(Long id);
    PurchaseBillDTO payPurchaseBill(Long id, Long paidBy);
    PurchaseBillDTO cancelPurchaseBill(Long id, String reason);
    
    // Purchase Bill Queries
    List<PurchaseBillDTO> getPurchaseBillsByPump(Long pumpId);
    Page<PurchaseBillDTO> getPurchaseBillsByPump(Long pumpId, Pageable pageable);
    List<PurchaseBillDTO> getPurchaseBillsBySupplier(Long supplierId);
    List<PurchaseBillDTO> getPurchaseBillsByStatus(PurchaseBill.Status status);
    List<PurchaseBillDTO> getPurchaseBillsByPaymentStatus(PurchaseBill.PaymentStatus paymentStatus);
    List<PurchaseBillDTO> getPurchaseBillsByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate);
    List<PurchaseBillDTO> getPurchaseBillsByBillType(PurchaseBill.BillType billType);
    
    // Approval and Workflow
    List<PurchaseBillDTO> getPendingApprovalBills(Long pumpId);
    List<PurchaseBillDTO> getOverdueBills(Long pumpId);
    List<PurchaseBillDTO> getBillsPendingReceipt(Long pumpId);
    
    // Reporting and Analytics
    Map<String, Object> getPurchaseBillDashboard(Long pumpId);
    Map<String, Object> getPurchaseBillSummaryByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> getPurchaseBillSummaryBySupplier(Long pumpId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> getPurchaseBillSummaryByFuelType(Long pumpId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> getPurchaseBillSummaryByStatus(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    // Bill Number Generation
    String generateBillNumber(Long pumpId, PurchaseBill.BillType billType);
    
    // Validation
    boolean isBillNumberUnique(String billNumber, Long excludeId);
    boolean canApproveBill(Long billId);
    boolean canReceiveBill(Long billId);
    boolean canPayBill(Long billId);
    boolean canCancelBill(Long billId);
}
