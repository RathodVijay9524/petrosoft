package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.*;
import com.vijay.petrosoft.domain.SaleTransaction;
import com.vijay.petrosoft.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    // Sale Transaction Endpoints
    @PostMapping("/transactions")
    public ResponseEntity<SaleTransactionDTO> createSaleTransaction(@Valid @RequestBody SaleTransactionDTO saleTransactionDTO) {
        SaleTransactionDTO createdTransaction = salesService.createSaleTransaction(saleTransactionDTO);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<SaleTransactionDTO> updateSaleTransaction(@PathVariable Long id, @Valid @RequestBody SaleTransactionDTO saleTransactionDTO) {
        SaleTransactionDTO updatedTransaction = salesService.updateSaleTransaction(id, saleTransactionDTO);
        return ResponseEntity.ok(updatedTransaction);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<SaleTransactionDTO> getSaleTransaction(@PathVariable Long id) {
        SaleTransactionDTO transaction = salesService.getSaleTransaction(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions/sale-number/{saleNumber}")
    public ResponseEntity<SaleTransactionDTO> getSaleTransactionBySaleNumber(@PathVariable String saleNumber) {
        SaleTransactionDTO transaction = salesService.getSaleTransactionBySaleNumber(saleNumber);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions/pump/{pumpId}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByPump(@PathVariable Long pumpId) {
        List<SaleTransactionDTO> sales = salesService.getSalesByPump(pumpId);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/transactions/shift/{shiftId}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByShift(@PathVariable Long shiftId) {
        List<SaleTransactionDTO> sales = salesService.getSalesByShift(shiftId);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/transactions/pump/{pumpId}/date-range")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByDateRange(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<SaleTransactionDTO> sales = salesService.getSalesByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/transactions/pump/{pumpId}/payment-method/{paymentMethod}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByPaymentMethod(
            @PathVariable Long pumpId,
            @PathVariable SaleTransaction.PaymentMethod paymentMethod) {
        List<SaleTransactionDTO> sales = salesService.getSalesByPaymentMethod(pumpId, paymentMethod);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/transactions/operator/{operatorId}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByOperator(
            @PathVariable Long operatorId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<SaleTransactionDTO> sales = salesService.getSalesByOperator(operatorId, startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/transactions/cashier/{cashierId}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByCashier(
            @PathVariable Long cashierId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<SaleTransactionDTO> sales = salesService.getSalesByCashier(cashierId, startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteSaleTransaction(@PathVariable Long id) {
        salesService.deleteSaleTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transactions/{id}/cancel")
    public ResponseEntity<SaleTransactionDTO> cancelSaleTransaction(@PathVariable Long id, @RequestParam String reason) {
        SaleTransactionDTO cancelledTransaction = salesService.cancelSaleTransaction(id, reason);
        return ResponseEntity.ok(cancelledTransaction);
    }

    @PostMapping("/transactions/{id}/refund")
    public ResponseEntity<SaleTransactionDTO> refundSaleTransaction(
            @PathVariable Long id,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason) {
        SaleTransactionDTO refundedTransaction = salesService.refundSaleTransaction(id, refundAmount, reason);
        return ResponseEntity.ok(refundedTransaction);
    }

    // Sale Item Endpoints
    @PostMapping("/items")
    public ResponseEntity<SaleItemDTO> createSaleItem(@Valid @RequestBody SaleItemDTO saleItemDTO) {
        SaleItemDTO createdItem = salesService.createSaleItem(saleItemDTO);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<SaleItemDTO> updateSaleItem(@PathVariable Long id, @Valid @RequestBody SaleItemDTO saleItemDTO) {
        SaleItemDTO updatedItem = salesService.updateSaleItem(id, saleItemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<SaleItemDTO> getSaleItem(@PathVariable Long id) {
        SaleItemDTO item = salesService.getSaleItem(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/items/transaction/{transactionId}")
    public ResponseEntity<List<SaleItemDTO>> getSaleItemsByTransaction(@PathVariable Long transactionId) {
        List<SaleItemDTO> items = salesService.getSaleItemsByTransaction(transactionId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/shift/{shiftId}")
    public ResponseEntity<List<SaleItemDTO>> getSaleItemsByShift(@PathVariable Long shiftId) {
        List<SaleItemDTO> items = salesService.getSaleItemsByShift(shiftId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Long id) {
        salesService.deleteSaleItem(id);
        return ResponseEntity.noContent().build();
    }

    // Shift Sales Summary Endpoints
    @PostMapping("/summaries")
    public ResponseEntity<ShiftSalesSummaryDTO> createShiftSalesSummary(@Valid @RequestBody ShiftSalesSummaryDTO summaryDTO) {
        ShiftSalesSummaryDTO createdSummary = salesService.createShiftSalesSummary(summaryDTO);
        return new ResponseEntity<>(createdSummary, HttpStatus.CREATED);
    }

    @PutMapping("/summaries/{id}")
    public ResponseEntity<ShiftSalesSummaryDTO> updateShiftSalesSummary(@PathVariable Long id, @Valid @RequestBody ShiftSalesSummaryDTO summaryDTO) {
        ShiftSalesSummaryDTO updatedSummary = salesService.updateShiftSalesSummary(id, summaryDTO);
        return ResponseEntity.ok(updatedSummary);
    }

    @GetMapping("/summaries/{id}")
    public ResponseEntity<ShiftSalesSummaryDTO> getShiftSalesSummary(@PathVariable Long id) {
        ShiftSalesSummaryDTO summary = salesService.getShiftSalesSummary(id);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summaries/shift/{shiftId}")
    public ResponseEntity<ShiftSalesSummaryDTO> getShiftSalesSummaryByShift(@PathVariable Long shiftId) {
        ShiftSalesSummaryDTO summary = salesService.getShiftSalesSummaryByShift(shiftId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summaries/pump/{pumpId}")
    public ResponseEntity<List<ShiftSalesSummaryDTO>> getShiftSalesSummariesByPump(@PathVariable Long pumpId) {
        List<ShiftSalesSummaryDTO> summaries = salesService.getShiftSalesSummariesByPump(pumpId);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/summaries/cashier/{cashierId}")
    public ResponseEntity<List<ShiftSalesSummaryDTO>> getShiftSalesSummariesByCashier(@PathVariable Long cashierId) {
        List<ShiftSalesSummaryDTO> summaries = salesService.getShiftSalesSummariesByCashier(cashierId);
        return ResponseEntity.ok(summaries);
    }

    @DeleteMapping("/summaries/{id}")
    public ResponseEntity<Void> deleteShiftSalesSummary(@PathVariable Long id) {
        salesService.deleteShiftSalesSummary(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/summaries/generate/{shiftId}")
    public ResponseEntity<ShiftSalesSummaryDTO> generateShiftSalesSummary(@PathVariable Long shiftId) {
        ShiftSalesSummaryDTO generatedSummary = salesService.generateShiftSalesSummary(shiftId);
        return ResponseEntity.ok(generatedSummary);
    }

    @PostMapping("/summaries/{id}/approve")
    public ResponseEntity<ShiftSalesSummaryDTO> approveShiftSalesSummary(@PathVariable Long id, @RequestParam Long approvedBy) {
        ShiftSalesSummaryDTO approvedSummary = salesService.approveShiftSalesSummary(id, approvedBy);
        return ResponseEntity.ok(approvedSummary);
    }

    @PostMapping("/summaries/{id}/submit")
    public ResponseEntity<ShiftSalesSummaryDTO> submitShiftSalesSummary(@PathVariable Long id) {
        ShiftSalesSummaryDTO submittedSummary = salesService.submitShiftSalesSummary(id);
        return ResponseEntity.ok(submittedSummary);
    }

    // Sales Entry Endpoints (from screenshot)
    @PostMapping("/entries/shift-sales")
    public ResponseEntity<SaleTransactionDTO> createShiftSalesEntry(@Valid @RequestBody ShiftSalesEntryDTO entryDTO) {
        SaleTransactionDTO createdEntry = salesService.createShiftSalesEntry(entryDTO);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }

    @PostMapping("/entries/cashier-sales")
    public ResponseEntity<SaleTransactionDTO> createCashierSalesEntry(@Valid @RequestBody CashierSalesEntryDTO entryDTO) {
        SaleTransactionDTO createdEntry = salesService.createCashierSalesEntry(entryDTO);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }

    @PostMapping("/entries/cash-credit-sales")
    public ResponseEntity<SaleTransactionDTO> createCashCreditSalesEntry(@Valid @RequestBody CashCreditSalesEntryDTO entryDTO) {
        SaleTransactionDTO createdEntry = salesService.createCashCreditSalesEntry(entryDTO);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }

    @PostMapping("/entries/tanker-credit-sales")
    public ResponseEntity<SaleTransactionDTO> createTankerCreditSalesEntry(@Valid @RequestBody TankerCreditSalesEntryDTO entryDTO) {
        SaleTransactionDTO createdEntry = salesService.createTankerCreditSalesEntry(entryDTO);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }

    // Analytics and Reports Endpoints
    @GetMapping("/dashboard/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getSalesDashboard(@PathVariable Long pumpId) {
        Map<String, Object> dashboard = salesService.getSalesDashboard(pumpId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/analytics/shift/{shiftId}")
    public ResponseEntity<Map<String, Object>> getShiftSalesAnalytics(@PathVariable Long shiftId) {
        Map<String, Object> analytics = salesService.getShiftSalesAnalytics(shiftId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/reports/summary/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getSalesSummaryByDateRange(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Map<String, Object> summary = salesService.getSalesSummaryByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reports/payment-method/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getSalesByPaymentMethodReport(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Map<String, Object> report = salesService.getSalesByPaymentMethod(pumpId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/fuel-type/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getSalesByFuelTypeReport(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Map<String, Object> report = salesService.getSalesByFuelType(pumpId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/operator/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getSalesByOperatorReport(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Map<String, Object> report = salesService.getSalesByOperatorReport(pumpId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/cashier/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getSalesByCashierReport(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Map<String, Object> report = salesService.getSalesByCashierReport(pumpId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    // Customer Credit Sales
    @GetMapping("/credit-sales/customer/{customerId}")
    public ResponseEntity<List<SaleTransactionDTO>> getCreditSalesByCustomer(@PathVariable Long customerId) {
        List<SaleTransactionDTO> creditSales = salesService.getCreditSalesByCustomer(customerId);
        return ResponseEntity.ok(creditSales);
    }

    @GetMapping("/outstanding-balance/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerOutstandingBalance(@PathVariable Long customerId) {
        BigDecimal outstandingBalance = salesService.getCustomerOutstandingBalance(customerId);
        Map<String, Object> result = Map.of("customerId", customerId, "outstandingBalance", outstandingBalance);
        return ResponseEntity.ok(result);
    }
}
