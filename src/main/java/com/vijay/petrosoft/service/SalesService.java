package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.*;
import com.vijay.petrosoft.domain.SaleTransaction;
import com.vijay.petrosoft.domain.ShiftSalesSummary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SalesService {

    // Sale Transaction Operations
    SaleTransactionDTO createSaleTransaction(SaleTransactionDTO saleTransactionDTO);
    SaleTransactionDTO updateSaleTransaction(Long id, SaleTransactionDTO saleTransactionDTO);
    SaleTransactionDTO getSaleTransaction(Long id);
    SaleTransactionDTO getSaleTransactionBySaleNumber(String saleNumber);
    List<SaleTransactionDTO> getSalesByPump(Long pumpId);
    List<SaleTransactionDTO> getSalesByShift(Long shiftId);
    List<SaleTransactionDTO> getSalesByDateRange(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    List<SaleTransactionDTO> getSalesByPaymentMethod(Long pumpId, SaleTransaction.PaymentMethod paymentMethod);
    void deleteSaleTransaction(Long id);
    SaleTransactionDTO cancelSaleTransaction(Long id, String reason);
    SaleTransactionDTO refundSaleTransaction(Long id, BigDecimal refundAmount, String reason);

    // Sale Item Operations
    SaleItemDTO createSaleItem(SaleItemDTO saleItemDTO);
    SaleItemDTO updateSaleItem(Long id, SaleItemDTO saleItemDTO);
    SaleItemDTO getSaleItem(Long id);
    List<SaleItemDTO> getSaleItemsByTransaction(Long saleTransactionId);
    List<SaleItemDTO> getSaleItemsByShift(Long shiftId);
    void deleteSaleItem(Long id);

    // Shift Sales Summary Operations
    ShiftSalesSummaryDTO createShiftSalesSummary(ShiftSalesSummaryDTO summaryDTO);
    ShiftSalesSummaryDTO updateShiftSalesSummary(Long id, ShiftSalesSummaryDTO summaryDTO);
    ShiftSalesSummaryDTO getShiftSalesSummary(Long id);
    ShiftSalesSummaryDTO getShiftSalesSummaryByShift(Long shiftId);
    List<ShiftSalesSummaryDTO> getShiftSalesSummariesByPump(Long pumpId);
    List<ShiftSalesSummaryDTO> getShiftSalesSummariesByCashier(Long cashierId);
    void deleteShiftSalesSummary(Long id);
    ShiftSalesSummaryDTO generateShiftSalesSummary(Long shiftId);
    ShiftSalesSummaryDTO approveShiftSalesSummary(Long id, Long approvedBy);
    ShiftSalesSummaryDTO submitShiftSalesSummary(Long id);

    // Sales Analytics and Reports
    Map<String, Object> getSalesDashboard(Long pumpId);
    Map<String, Object> getShiftSalesAnalytics(Long shiftId);
    Map<String, Object> getSalesSummaryByDateRange(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getSalesByPaymentMethod(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getSalesByFuelType(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getSalesByOperatorReport(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getSalesByCashierReport(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Additional methods for operator and cashier sales
    List<SaleTransactionDTO> getSalesByOperator(Long operatorId, LocalDateTime startDate, LocalDateTime endDate);
    List<SaleTransactionDTO> getSalesByCashier(Long cashierId, LocalDateTime startDate, LocalDateTime endDate);

    // Sales Entry Operations (from screenshot)
    SaleTransactionDTO createShiftSalesEntry(ShiftSalesEntryDTO entryDTO);
    SaleTransactionDTO createCashierSalesEntry(CashierSalesEntryDTO entryDTO);
    SaleTransactionDTO createCashCreditSalesEntry(CashCreditSalesEntryDTO entryDTO);
    SaleTransactionDTO createTankerCreditSalesEntry(TankerCreditSalesEntryDTO entryDTO);

    // Helper Methods
    String generateSaleNumber(Long pumpId);
    BigDecimal calculateTotalAmount(List<SaleItemDTO> saleItems);
    Map<String, BigDecimal> getSalesTotalsByPaymentMethod(Long shiftId);
    Map<String, BigDecimal> getSalesTotalsByFuelType(Long shiftId);
    List<SaleTransactionDTO> getCreditSalesByCustomer(Long customerId);
    BigDecimal getCustomerOutstandingBalance(Long customerId);
}
