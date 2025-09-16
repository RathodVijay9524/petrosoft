package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.domain.PurchaseBill;
import com.vijay.petrosoft.dto.PurchaseBillDTO;
import com.vijay.petrosoft.service.PurchaseBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-bills")
@RequiredArgsConstructor
@Slf4j
public class PurchaseBillController {
    
    private final PurchaseBillService purchaseBillService;
    
    @PostMapping
    public ResponseEntity<PurchaseBillDTO> createPurchaseBill(@Valid @RequestBody PurchaseBillDTO purchaseBillDTO) {
        log.info("Creating purchase bill: {}", purchaseBillDTO.getBillNumber());
        PurchaseBillDTO createdBill = purchaseBillService.createPurchaseBill(purchaseBillDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBill);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseBillDTO> getPurchaseBillById(@PathVariable Long id) {
        log.info("Getting purchase bill by ID: {}", id);
        PurchaseBillDTO bill = purchaseBillService.getPurchaseBillById(id);
        return ResponseEntity.ok(bill);
    }
    
    @GetMapping("/bill-number/{billNumber}")
    public ResponseEntity<PurchaseBillDTO> getPurchaseBillByBillNumber(@PathVariable String billNumber) {
        log.info("Getting purchase bill by bill number: {}", billNumber);
        PurchaseBillDTO bill = purchaseBillService.getPurchaseBillByBillNumber(billNumber);
        return ResponseEntity.ok(bill);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseBillDTO> updatePurchaseBill(@PathVariable Long id, @Valid @RequestBody PurchaseBillDTO purchaseBillDTO) {
        log.info("Updating purchase bill with ID: {}", id);
        PurchaseBillDTO updatedBill = purchaseBillService.updatePurchaseBill(id, purchaseBillDTO);
        return ResponseEntity.ok(updatedBill);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseBill(@PathVariable Long id) {
        log.info("Deleting purchase bill with ID: {}", id);
        purchaseBillService.deletePurchaseBill(id);
        return ResponseEntity.noContent().build();
    }
    
    // Status Management Endpoints
    @PostMapping("/{id}/submit")
    public ResponseEntity<PurchaseBillDTO> submitPurchaseBill(@PathVariable Long id) {
        log.info("Submitting purchase bill with ID: {}", id);
        PurchaseBillDTO updatedBill = purchaseBillService.submitPurchaseBill(id);
        return ResponseEntity.ok(updatedBill);
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<PurchaseBillDTO> approvePurchaseBill(@PathVariable Long id, @RequestParam Long approvedBy) {
        log.info("Approving purchase bill with ID: {} by user: {}", id, approvedBy);
        PurchaseBillDTO updatedBill = purchaseBillService.approvePurchaseBill(id, approvedBy);
        return ResponseEntity.ok(updatedBill);
    }
    
    @PostMapping("/{id}/receive")
    public ResponseEntity<PurchaseBillDTO> receivePurchaseBill(@PathVariable Long id, @RequestParam Long receivedBy) {
        log.info("Receiving purchase bill with ID: {} by user: {}", id, receivedBy);
        PurchaseBillDTO updatedBill = purchaseBillService.receivePurchaseBill(id, receivedBy);
        return ResponseEntity.ok(updatedBill);
    }
    
    @PostMapping("/{id}/invoice")
    public ResponseEntity<PurchaseBillDTO> invoicePurchaseBill(@PathVariable Long id) {
        log.info("Invoicing purchase bill with ID: {}", id);
        PurchaseBillDTO updatedBill = purchaseBillService.invoicePurchaseBill(id);
        return ResponseEntity.ok(updatedBill);
    }
    
    @PostMapping("/{id}/pay")
    public ResponseEntity<PurchaseBillDTO> payPurchaseBill(@PathVariable Long id, @RequestParam Long paidBy) {
        log.info("Paying purchase bill with ID: {} by user: {}", id, paidBy);
        PurchaseBillDTO updatedBill = purchaseBillService.payPurchaseBill(id, paidBy);
        return ResponseEntity.ok(updatedBill);
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseBillDTO> cancelPurchaseBill(@PathVariable Long id, @RequestParam String reason) {
        log.info("Cancelling purchase bill with ID: {} for reason: {}", id, reason);
        PurchaseBillDTO updatedBill = purchaseBillService.cancelPurchaseBill(id, reason);
        return ResponseEntity.ok(updatedBill);
    }
    
    // Query Endpoints
    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<PurchaseBillDTO>> getPurchaseBillsByPump(@PathVariable Long pumpId) {
        log.info("Getting purchase bills by pump ID: {}", pumpId);
        List<PurchaseBillDTO> bills = purchaseBillService.getPurchaseBillsByPump(pumpId);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/pump/{pumpId}/paginated")
    public ResponseEntity<Page<PurchaseBillDTO>> getPurchaseBillsByPumpPaginated(@PathVariable Long pumpId, Pageable pageable) {
        log.info("Getting purchase bills by pump ID: {} with pagination", pumpId);
        Page<PurchaseBillDTO> bills = purchaseBillService.getPurchaseBillsByPump(pumpId, pageable);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseBillDTO>> getPurchaseBillsBySupplier(@PathVariable Long supplierId) {
        log.info("Getting purchase bills by supplier ID: {}", supplierId);
        List<PurchaseBillDTO> bills = purchaseBillService.getPurchaseBillsBySupplier(supplierId);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseBillDTO>> getPurchaseBillsByStatus(@PathVariable PurchaseBill.Status status) {
        log.info("Getting purchase bills by status: {}", status);
        List<PurchaseBillDTO> bills = purchaseBillService.getPurchaseBillsByStatus(status);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<PurchaseBillDTO>> getPurchaseBillsByPaymentStatus(@PathVariable PurchaseBill.PaymentStatus paymentStatus) {
        log.info("Getting purchase bills by payment status: {}", paymentStatus);
        List<PurchaseBillDTO> bills = purchaseBillService.getPurchaseBillsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<PurchaseBillDTO>> getPurchaseBillsByDateRange(
            @RequestParam Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting purchase bills by date range: {} to {} for pump: {}", startDate, endDate, pumpId);
        List<PurchaseBillDTO> bills = purchaseBillService.getPurchaseBillsByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/bill-type/{billType}")
    public ResponseEntity<List<PurchaseBillDTO>> getPurchaseBillsByBillType(@PathVariable PurchaseBill.BillType billType) {
        log.info("Getting purchase bills by bill type: {}", billType);
        List<PurchaseBillDTO> bills = purchaseBillService.getPurchaseBillsByBillType(billType);
        return ResponseEntity.ok(bills);
    }
    
    // Workflow Endpoints
    @GetMapping("/pump/{pumpId}/pending-approval")
    public ResponseEntity<List<PurchaseBillDTO>> getPendingApprovalBills(@PathVariable Long pumpId) {
        log.info("Getting pending approval bills for pump: {}", pumpId);
        List<PurchaseBillDTO> bills = purchaseBillService.getPendingApprovalBills(pumpId);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/pump/{pumpId}/overdue")
    public ResponseEntity<List<PurchaseBillDTO>> getOverdueBills(@PathVariable Long pumpId) {
        log.info("Getting overdue bills for pump: {}", pumpId);
        List<PurchaseBillDTO> bills = purchaseBillService.getOverdueBills(pumpId);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/pump/{pumpId}/pending-receipt")
    public ResponseEntity<List<PurchaseBillDTO>> getBillsPendingReceipt(@PathVariable Long pumpId) {
        log.info("Getting bills pending receipt for pump: {}", pumpId);
        List<PurchaseBillDTO> bills = purchaseBillService.getBillsPendingReceipt(pumpId);
        return ResponseEntity.ok(bills);
    }
    
    // Reporting Endpoints
    @GetMapping("/pump/{pumpId}/dashboard")
    public ResponseEntity<Map<String, Object>> getPurchaseBillDashboard(@PathVariable Long pumpId) {
        log.info("Getting purchase bill dashboard for pump: {}", pumpId);
        Map<String, Object> dashboard = purchaseBillService.getPurchaseBillDashboard(pumpId);
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/pump/{pumpId}/summary/date-range")
    public ResponseEntity<Map<String, Object>> getPurchaseBillSummaryByDateRange(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting purchase bill summary by date range for pump: {}", pumpId);
        Map<String, Object> summary = purchaseBillService.getPurchaseBillSummaryByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/pump/{pumpId}/summary/supplier")
    public ResponseEntity<Map<String, Object>> getPurchaseBillSummaryBySupplier(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting purchase bill summary by supplier for pump: {}", pumpId);
        Map<String, Object> summary = purchaseBillService.getPurchaseBillSummaryBySupplier(pumpId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/pump/{pumpId}/summary/fuel-type")
    public ResponseEntity<Map<String, Object>> getPurchaseBillSummaryByFuelType(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting purchase bill summary by fuel type for pump: {}", pumpId);
        Map<String, Object> summary = purchaseBillService.getPurchaseBillSummaryByFuelType(pumpId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/pump/{pumpId}/summary/status")
    public ResponseEntity<Map<String, Object>> getPurchaseBillSummaryByStatus(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting purchase bill summary by status for pump: {}", pumpId);
        Map<String, Object> summary = purchaseBillService.getPurchaseBillSummaryByStatus(pumpId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
    
    // Utility Endpoints
    @GetMapping("/generate-bill-number")
    public ResponseEntity<Map<String, String>> generateBillNumber(@RequestParam Long pumpId, @RequestParam PurchaseBill.BillType billType) {
        log.info("Generating bill number for pump: {} and type: {}", pumpId, billType);
        String billNumber = purchaseBillService.generateBillNumber(pumpId, billType);
        return ResponseEntity.ok(Map.of("billNumber", billNumber));
    }
    
    @GetMapping("/validate/bill-number")
    public ResponseEntity<Map<String, Boolean>> validateBillNumber(@RequestParam String billNumber, @RequestParam(required = false) Long excludeId) {
        log.info("Validating bill number: {}", billNumber);
        boolean isUnique = purchaseBillService.isBillNumberUnique(billNumber, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }
    
    @GetMapping("/validate/approve/{billId}")
    public ResponseEntity<Map<String, Boolean>> canApproveBill(@PathVariable Long billId) {
        log.info("Checking if bill can be approved: {}", billId);
        boolean canApprove = purchaseBillService.canApproveBill(billId);
        return ResponseEntity.ok(Map.of("canApprove", canApprove));
    }
    
    @GetMapping("/validate/receive/{billId}")
    public ResponseEntity<Map<String, Boolean>> canReceiveBill(@PathVariable Long billId) {
        log.info("Checking if bill can be received: {}", billId);
        boolean canReceive = purchaseBillService.canReceiveBill(billId);
        return ResponseEntity.ok(Map.of("canReceive", canReceive));
    }
    
    @GetMapping("/validate/pay/{billId}")
    public ResponseEntity<Map<String, Boolean>> canPayBill(@PathVariable Long billId) {
        log.info("Checking if bill can be paid: {}", billId);
        boolean canPay = purchaseBillService.canPayBill(billId);
        return ResponseEntity.ok(Map.of("canPay", canPay));
    }
    
    @GetMapping("/validate/cancel/{billId}")
    public ResponseEntity<Map<String, Boolean>> canCancelBill(@PathVariable Long billId) {
        log.info("Checking if bill can be cancelled: {}", billId);
        boolean canCancel = purchaseBillService.canCancelBill(billId);
        return ResponseEntity.ok(Map.of("canCancel", canCancel));
    }
}
