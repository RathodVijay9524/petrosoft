package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.domain.Voucher;
import com.vijay.petrosoft.dto.VoucherDTO;
import com.vijay.petrosoft.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VoucherController {

    private final VoucherService voucherService;

    // CRUD Operations
    @PostMapping
    public ResponseEntity<VoucherDTO> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createVoucher(voucherDTO);
            log.info("Voucher created with number: {}", voucherDTO.getVoucherNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getVoucherById(@PathVariable Long id) {
        Optional<VoucherDTO> voucher = voucherService.getVoucherById(id);
        return voucher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{voucherNumber}")
    public ResponseEntity<VoucherDTO> getVoucherByNumber(@PathVariable String voucherNumber) {
        Optional<VoucherDTO> voucher = voucherService.getVoucherByNumber(voucherNumber);
        return voucher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<VoucherDTO>> getAllVouchers() {
        List<VoucherDTO> vouchers = voucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByPumpId(@PathVariable Long pumpId) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByPumpId(pumpId);
        return ResponseEntity.ok(vouchers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> updateVoucher(@PathVariable Long id, @Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO updatedVoucher = voucherService.updateVoucher(id, voucherDTO);
            log.info("Voucher updated for ID: {}", id);
            return ResponseEntity.ok(updatedVoucher);
        } catch (Exception e) {
            log.error("Error updating voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        try {
            voucherService.deleteVoucher(id);
            log.info("Voucher deleted for ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Voucher Type Management
    @GetMapping("/type/{voucherType}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByType(@PathVariable Voucher.VoucherType voucherType) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByType(voucherType);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/pump/{pumpId}/type/{voucherType}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByPumpIdAndType(
            @PathVariable Long pumpId, 
            @PathVariable Voucher.VoucherType voucherType) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByPumpIdAndType(pumpId, voucherType);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByStatus(@PathVariable Voucher.VoucherStatus status) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByStatus(status);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/pump/{pumpId}/status/{status}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByPumpIdAndStatus(
            @PathVariable Long pumpId, 
            @PathVariable Voucher.VoucherStatus status) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByPumpIdAndStatus(pumpId, status);
        return ResponseEntity.ok(vouchers);
    }

    // Voucher Workflow
    @PutMapping("/{id}/approve")
    public ResponseEntity<VoucherDTO> approveVoucher(
            @PathVariable Long id, 
            @RequestParam Long approvedBy) {
        try {
            VoucherDTO approvedVoucher = voucherService.approveVoucher(id, approvedBy);
            return ResponseEntity.ok(approvedVoucher);
        } catch (Exception e) {
            log.error("Error approving voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/post")
    public ResponseEntity<VoucherDTO> postVoucher(
            @PathVariable Long id, 
            @RequestParam Long postedBy) {
        try {
            VoucherDTO postedVoucher = voucherService.postVoucher(id, postedBy);
            return ResponseEntity.ok(postedVoucher);
        } catch (Exception e) {
            log.error("Error posting voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<VoucherDTO> cancelVoucher(
            @PathVariable Long id, 
            @RequestParam Long cancelledBy,
            @RequestParam String reason) {
        try {
            VoucherDTO cancelledVoucher = voucherService.cancelVoucher(id, cancelledBy, reason);
            return ResponseEntity.ok(cancelledVoucher);
        } catch (Exception e) {
            log.error("Error cancelling voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/uncancel")
    public ResponseEntity<VoucherDTO> uncancelVoucher(@PathVariable Long id) {
        try {
            VoucherDTO uncancelledVoucher = voucherService.uncancelVoucher(id);
            return ResponseEntity.ok(uncancelledVoucher);
        } catch (Exception e) {
            log.error("Error uncancelling voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Date Range Queries
    @GetMapping("/date-range")
    public ResponseEntity<List<VoucherDTO>> getVouchersByDateRange(
            @RequestParam Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/type-date-range")
    public ResponseEntity<List<VoucherDTO>> getVouchersByTypeAndDateRange(
            @RequestParam Long pumpId,
            @RequestParam Voucher.VoucherType voucherType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByTypeAndDateRange(pumpId, voucherType, startDate, endDate);
        return ResponseEntity.ok(vouchers);
    }

    // Status Management
    @GetMapping("/posted/{pumpId}")
    public ResponseEntity<List<VoucherDTO>> getPostedVouchers(@PathVariable Long pumpId) {
        List<VoucherDTO> vouchers = voucherService.getPostedVouchers(pumpId);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/posted/{pumpId}/type/{voucherType}")
    public ResponseEntity<List<VoucherDTO>> getPostedVouchersByType(
            @PathVariable Long pumpId, 
            @PathVariable Voucher.VoucherType voucherType) {
        List<VoucherDTO> vouchers = voucherService.getPostedVouchersByType(pumpId, voucherType);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/unposted/{pumpId}")
    public ResponseEntity<List<VoucherDTO>> getUnpostedVouchers(@PathVariable Long pumpId) {
        List<VoucherDTO> vouchers = voucherService.getUnpostedVouchers(pumpId);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/cancelled/{pumpId}")
    public ResponseEntity<List<VoucherDTO>> getCancelledVouchers(@PathVariable Long pumpId) {
        List<VoucherDTO> vouchers = voucherService.getCancelledVouchers(pumpId);
        return ResponseEntity.ok(vouchers);
    }

    // User Management
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByUser(
            @RequestParam Long pumpId,
            @PathVariable Long userId) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByUser(pumpId, userId);
        return ResponseEntity.ok(vouchers);
    }

    // Party Management
    @GetMapping("/party/{partyName}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByPartyName(
            @RequestParam Long pumpId,
            @PathVariable String partyName) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByPartyName(pumpId, partyName);
        return ResponseEntity.ok(vouchers);
    }

    // Payment Mode Management
    @GetMapping("/payment-mode")
    public ResponseEntity<List<VoucherDTO>> getVouchersByPaymentMode(
            @RequestParam Long pumpId,
            @RequestParam String paymentMode) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByPaymentMode(pumpId, paymentMode);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/payment-mode/date-range")
    public ResponseEntity<List<VoucherDTO>> getVouchersByPaymentModeAndDateRange(
            @RequestParam Long pumpId,
            @RequestParam String paymentMode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByPaymentModeAndDateRange(pumpId, paymentMode, startDate, endDate);
        return ResponseEntity.ok(vouchers);
    }

    // Cheque Management
    @GetMapping("/cheque/{chequeNumber}")
    public ResponseEntity<List<VoucherDTO>> getVouchersByChequeNumber(
            @RequestParam Long pumpId,
            @PathVariable String chequeNumber) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByChequeNumber(pumpId, chequeNumber);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/cheque-date")
    public ResponseEntity<List<VoucherDTO>> getVouchersByChequeDate(
            @RequestParam Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate chequeDate) {
        List<VoucherDTO> vouchers = voucherService.getVouchersByChequeDate(pumpId, chequeDate);
        return ResponseEntity.ok(vouchers);
    }

    // Reconciliation Management
    @GetMapping("/unreconciled/{pumpId}")
    public ResponseEntity<List<VoucherDTO>> getUnreconciledVouchers(@PathVariable Long pumpId) {
        List<VoucherDTO> vouchers = voucherService.getUnreconciledVouchers(pumpId);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/reconciled/{pumpId}")
    public ResponseEntity<List<VoucherDTO>> getReconciledVouchers(@PathVariable Long pumpId) {
        List<VoucherDTO> vouchers = voucherService.getReconciledVouchers(pumpId);
        return ResponseEntity.ok(vouchers);
    }

    @PutMapping("/{id}/reconcile")
    public ResponseEntity<VoucherDTO> reconcileVoucher(
            @PathVariable Long id, 
            @RequestParam Long reconciledBy) {
        try {
            VoucherDTO reconciledVoucher = voucherService.reconcileVoucher(id, reconciledBy);
            return ResponseEntity.ok(reconciledVoucher);
        } catch (Exception e) {
            log.error("Error reconciling voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/unreconcile")
    public ResponseEntity<VoucherDTO> unreconcileVoucher(@PathVariable Long id) {
        try {
            VoucherDTO unreconciledVoucher = voucherService.unreconcileVoucher(id);
            return ResponseEntity.ok(unreconciledVoucher);
        } catch (Exception e) {
            log.error("Error unreconciling voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Voucher Number Generation and Validation
    @GetMapping("/generate-number")
    public ResponseEntity<String> generateVoucherNumber(
            @RequestParam Voucher.VoucherType voucherType,
            @RequestParam Long pumpId) {
        String voucherNumber = voucherService.generateVoucherNumber(voucherType, pumpId);
        return ResponseEntity.ok(voucherNumber);
    }

    @GetMapping("/validate-number/{voucherNumber}")
    public ResponseEntity<Boolean> validateVoucherNumber(@PathVariable String voucherNumber) {
        boolean isValid = voucherService.validateVoucherNumber(voucherNumber);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/validate-entry")
    public ResponseEntity<Boolean> validateDoubleEntry(@Valid @RequestBody VoucherDTO voucherDTO) {
        boolean isValid = voucherService.validateDoubleEntry(voucherDTO);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/validate-balances")
    public ResponseEntity<Boolean> validateVoucherBalances(@Valid @RequestBody VoucherDTO voucherDTO) {
        boolean isValid = voucherService.validateVoucherBalances(voucherDTO);
        return ResponseEntity.ok(isValid);
    }

    // Amount Calculations
    @PostMapping("/calculate-debit")
    public ResponseEntity<java.math.BigDecimal> calculateTotalDebit(@Valid @RequestBody VoucherDTO voucherDTO) {
        java.math.BigDecimal totalDebit = voucherService.calculateTotalDebit(voucherDTO);
        return ResponseEntity.ok(totalDebit);
    }

    @PostMapping("/calculate-credit")
    public ResponseEntity<java.math.BigDecimal> calculateTotalCredit(@Valid @RequestBody VoucherDTO voucherDTO) {
        java.math.BigDecimal totalCredit = voucherService.calculateTotalCredit(voucherDTO);
        return ResponseEntity.ok(totalCredit);
    }

    @GetMapping("/total-amount")
    public ResponseEntity<java.math.BigDecimal> getTotalAmountByType(
            @RequestParam Long pumpId,
            @RequestParam Voucher.VoucherType voucherType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        java.math.BigDecimal totalAmount = voucherService.getTotalAmountByType(pumpId, voucherType, startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }

    // Search Operations
    @GetMapping("/search/number")
    public ResponseEntity<List<VoucherDTO>> searchVouchersByNumber(
            @RequestParam Long pumpId,
            @RequestParam String voucherNumber) {
        List<VoucherDTO> vouchers = voucherService.searchVouchersByNumber(pumpId, voucherNumber);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/search/reference")
    public ResponseEntity<List<VoucherDTO>> searchVouchersByReference(
            @RequestParam Long pumpId,
            @RequestParam String reference) {
        List<VoucherDTO> vouchers = voucherService.searchVouchersByReference(pumpId, reference);
        return ResponseEntity.ok(vouchers);
    }

    // Analytics and Reports
    @GetMapping("/analytics/{pumpId}")
    public ResponseEntity<Map<String, Object>> getVoucherAnalytics(@PathVariable Long pumpId) {
        Map<String, Object> analytics = voucherService.getVoucherAnalytics(pumpId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/count/type/{pumpId}")
    public ResponseEntity<Map<String, Long>> getVoucherCountByType(@PathVariable Long pumpId) {
        Map<String, Long> counts = voucherService.getVoucherCountByType(pumpId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/amount/type/{pumpId}")
    public ResponseEntity<Map<String, java.math.BigDecimal>> getVoucherAmountByType(
            @PathVariable Long pumpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, java.math.BigDecimal> amounts = voucherService.getVoucherAmountByType(pumpId, startDate, endDate);
        return ResponseEntity.ok(amounts);
    }

    // Bulk Operations
    @PostMapping("/bulk-post")
    public ResponseEntity<List<VoucherDTO>> bulkPostVouchers(
            @RequestBody List<Long> voucherIds,
            @RequestParam Long postedBy) {
        try {
            List<VoucherDTO> postedVouchers = voucherService.bulkPostVouchers(voucherIds, postedBy);
            return ResponseEntity.ok(postedVouchers);
        } catch (Exception e) {
            log.error("Error bulk posting vouchers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/bulk-cancel")
    public ResponseEntity<List<VoucherDTO>> bulkCancelVouchers(
            @RequestBody List<Long> voucherIds,
            @RequestParam Long cancelledBy,
            @RequestParam String reason) {
        try {
            List<VoucherDTO> cancelledVouchers = voucherService.bulkCancelVouchers(voucherIds, cancelledBy, reason);
            return ResponseEntity.ok(cancelledVouchers);
        } catch (Exception e) {
            log.error("Error bulk cancelling vouchers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Specific Voucher Types
    @PostMapping("/customer-receipt")
    public ResponseEntity<VoucherDTO> createCustomerReceipt(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createCustomerReceipt(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating customer receipt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<VoucherDTO> createPaymentVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createPaymentVoucher(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating payment voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/misc-receipt")
    public ResponseEntity<VoucherDTO> createMiscellaneousReceipt(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createMiscellaneousReceipt(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating miscellaneous receipt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/journal")
    public ResponseEntity<VoucherDTO> createJournalVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createJournalVoucher(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating journal voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/cash-deposit")
    public ResponseEntity<VoucherDTO> createCashDeposit(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createCashDeposit(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating cash deposit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/cash-withdrawal")
    public ResponseEntity<VoucherDTO> createCashWithdrawal(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createCashWithdrawal(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating cash withdrawal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/contra")
    public ResponseEntity<VoucherDTO> createContraVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createContraVoucher(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating contra voucher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/cheque-return")
    public ResponseEntity<VoucherDTO> createChequeReturn(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createChequeReturn(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating cheque return: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Voucher Templates
    @GetMapping("/templates/{voucherType}")
    public ResponseEntity<List<VoucherDTO>> getVoucherTemplates(@PathVariable Voucher.VoucherType voucherType) {
        List<VoucherDTO> templates = voucherService.getVoucherTemplates(voucherType);
        return ResponseEntity.ok(templates);
    }

    @PostMapping("/from-template")
    public ResponseEntity<VoucherDTO> createVoucherFromTemplate(
            @RequestParam Long templateId,
            @RequestParam Long pumpId,
            @RequestParam Long userId) {
        try {
            VoucherDTO createdVoucher = voucherService.createVoucherFromTemplate(templateId, pumpId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        } catch (Exception e) {
            log.error("Error creating voucher from template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
