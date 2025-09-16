package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.SaleTransaction;
import com.vijay.petrosoft.dto.SaleTransactionDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleTransactionService {
    SaleTransactionDTO createSale(SaleTransactionDTO saleDTO);
    SaleTransactionDTO updateSale(Long id, SaleTransactionDTO saleDTO);
    Optional<SaleTransactionDTO> getSaleById(Long id);
    List<SaleTransactionDTO> getAllSales();
    void deleteSale(Long id);
    List<SaleTransactionDTO> getSalesByPumpId(Long pumpId);
    List<SaleTransactionDTO> getSalesByShiftId(Long shiftId);
    List<SaleTransactionDTO> getSalesByDateRange(LocalDate startDate, LocalDate endDate);
    List<SaleTransactionDTO> getSalesByCustomerId(Long customerId);
    List<SaleTransactionDTO> getCreditSales();
    List<SaleTransactionDTO> getCashSales();
    BigDecimal getTotalSalesByDate(LocalDate date);
    BigDecimal getTotalSalesByShift(Long shiftId);
    SaleTransactionDTO processFuelSale(Long nozzleId, BigDecimal quantity, Long customerId, boolean isCredit);
}
