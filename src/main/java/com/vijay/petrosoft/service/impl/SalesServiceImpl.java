package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.dto.*;
import com.vijay.petrosoft.domain.*;
import com.vijay.petrosoft.repository.*;
import com.vijay.petrosoft.service.SalesService;
import com.vijay.petrosoft.exception.ResourceNotFoundException;
import com.vijay.petrosoft.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SalesServiceImpl implements SalesService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ShiftSalesSummaryRepository shiftSalesSummaryRepository;
    private final ShiftRepository shiftRepository;
    private final CustomerRepository customerRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final NozzleRepository nozzleRepository;

    // Sale Transaction Operations
    @Override
    public SaleTransactionDTO createSaleTransaction(SaleTransactionDTO saleTransactionDTO) {
        log.info("Creating sale transaction for pump: {}", saleTransactionDTO.getPumpId());
        
        SaleTransaction saleTransaction = SaleTransaction.builder()
                .pumpId(saleTransactionDTO.getPumpId())
                .shift(shiftRepository.findById(saleTransactionDTO.getShiftId())
                        .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + saleTransactionDTO.getShiftId())))
                .nozzle(nozzleRepository.findById(saleTransactionDTO.getNozzleId()).orElse(null))
                .fuelType(fuelTypeRepository.findById(saleTransactionDTO.getFuelTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Fuel type not found with id: " + saleTransactionDTO.getFuelTypeId())))
                .customer(customerRepository.findById(saleTransactionDTO.getCustomerId()).orElse(null))
                .saleNumber(generateSaleNumber(saleTransactionDTO.getPumpId()))
                .quantity(saleTransactionDTO.getQuantity())
                .rate(saleTransactionDTO.getRate())
                .amount(saleTransactionDTO.getAmount())
                .discountAmount(saleTransactionDTO.getDiscountAmount())
                .taxAmount(saleTransactionDTO.getTaxAmount())
                .totalAmount(saleTransactionDTO.getTotalAmount())
                .paymentMethod(saleTransactionDTO.getPaymentMethod())
                .saleType(saleTransactionDTO.getSaleType())
                .status(saleTransactionDTO.getStatus())
                .transactedAt(saleTransactionDTO.getTransactedAt())
                .operatorId(saleTransactionDTO.getOperatorId())
                .cashierId(saleTransactionDTO.getCashierId())
                .vehicleNumber(saleTransactionDTO.getVehicleNumber())
                .driverName(saleTransactionDTO.getDriverName())
                .notes(saleTransactionDTO.getNotes())
                .cardLastFour(saleTransactionDTO.getCardLastFour())
                .cardType(saleTransactionDTO.getCardType())
                .transactionReference(saleTransactionDTO.getTransactionReference())
                .build();

        SaleTransaction savedTransaction = saleRepository.save(saleTransaction);
        return convertToSaleTransactionDTO(savedTransaction);
    }

    @Override
    public SaleTransactionDTO updateSaleTransaction(Long id, SaleTransactionDTO saleTransactionDTO) {
        log.info("Updating sale transaction: {}", id);
        
        SaleTransaction saleTransaction = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale transaction not found with id: " + id));

        // Update fields
        saleTransaction.setQuantity(saleTransactionDTO.getQuantity());
        saleTransaction.setRate(saleTransactionDTO.getRate());
        saleTransaction.setAmount(saleTransactionDTO.getAmount());
        saleTransaction.setDiscountAmount(saleTransactionDTO.getDiscountAmount());
        saleTransaction.setTaxAmount(saleTransactionDTO.getTaxAmount());
        saleTransaction.setTotalAmount(saleTransactionDTO.getTotalAmount());
        saleTransaction.setVehicleNumber(saleTransactionDTO.getVehicleNumber());
        saleTransaction.setDriverName(saleTransactionDTO.getDriverName());
        saleTransaction.setNotes(saleTransactionDTO.getNotes());

        SaleTransaction updatedTransaction = saleRepository.save(saleTransaction);
        return convertToSaleTransactionDTO(updatedTransaction);
    }

    @Override
    public SaleTransactionDTO getSaleTransaction(Long id) {
        SaleTransaction saleTransaction = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale transaction not found with id: " + id));
        return convertToSaleTransactionDTO(saleTransaction);
    }

    @Override
    public SaleTransactionDTO getSaleTransactionBySaleNumber(String saleNumber) {
        SaleTransaction saleTransaction = saleRepository.findBySaleNumber(saleNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sale transaction not found with sale number: " + saleNumber));
        return convertToSaleTransactionDTO(saleTransaction);
    }

    @Override
    public List<SaleTransactionDTO> getSalesByPump(Long pumpId) {
        List<SaleTransaction> sales = saleRepository.findByPumpIdOrderByTransactedAtDesc(pumpId);
        return sales.stream()
                .map(this::convertToSaleTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleTransactionDTO> getSalesByShift(Long shiftId) {
        List<SaleTransaction> sales = saleRepository.findByShiftIdOrderByTransactedAtDesc(shiftId);
        return sales.stream()
                .map(this::convertToSaleTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleTransactionDTO> getSalesByDateRange(Long pumpId, LocalDateTime startDate, LocalDateTime endDate) {
        List<SaleTransaction> sales = saleRepository.findByPumpIdAndTransactedAtBetween(pumpId, startDate, endDate);
        return sales.stream()
                .map(this::convertToSaleTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleTransactionDTO> getSalesByOperator(Long operatorId, LocalDateTime startDate, LocalDateTime endDate) {
        List<SaleTransaction> sales = saleRepository.findByOperatorIdAndTransactedAtBetween(operatorId, startDate, endDate);
        return sales.stream()
                .map(this::convertToSaleTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleTransactionDTO> getSalesByCashier(Long cashierId, LocalDateTime startDate, LocalDateTime endDate) {
        List<SaleTransaction> sales = saleRepository.findByCashierIdAndTransactedAtBetween(cashierId, startDate, endDate);
        return sales.stream()
                .map(this::convertToSaleTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleTransactionDTO> getSalesByPaymentMethod(Long pumpId, SaleTransaction.PaymentMethod paymentMethod) {
        List<SaleTransaction> sales = saleRepository.findByPumpIdAndPaymentMethod(pumpId, paymentMethod);
        return sales.stream()
                .map(this::convertToSaleTransactionDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteSaleTransaction(Long id) {
        log.info("Deleting sale transaction: {}", id);
        if (!saleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sale transaction not found with id: " + id);
        }
        saleRepository.deleteById(id);
    }

    @Override
    public SaleTransactionDTO cancelSaleTransaction(Long id, String reason) {
        log.info("Cancelling sale transaction: {} with reason: {}", id, reason);
        
        SaleTransaction saleTransaction = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale transaction not found with id: " + id));

        if (saleTransaction.getStatus() != SaleTransaction.Status.COMPLETED) {
            throw new BusinessLogicException("Only completed transactions can be cancelled");
        }

        saleTransaction.setStatus(SaleTransaction.Status.CANCELLED);
        saleTransaction.setNotes(saleTransaction.getNotes() + " [CANCELLED: " + reason + "]");

        SaleTransaction cancelledTransaction = saleRepository.save(saleTransaction);
        return convertToSaleTransactionDTO(cancelledTransaction);
    }

    @Override
    public SaleTransactionDTO refundSaleTransaction(Long id, BigDecimal refundAmount, String reason) {
        log.info("Processing refund for sale transaction: {} amount: {}", id, refundAmount);
        
        SaleTransaction saleTransaction = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale transaction not found with id: " + id));

        if (saleTransaction.getStatus() != SaleTransaction.Status.COMPLETED) {
            throw new BusinessLogicException("Only completed transactions can be refunded");
        }

        if (refundAmount.compareTo(saleTransaction.getTotalAmount()) > 0) {
            throw new BusinessLogicException("Refund amount cannot exceed transaction amount");
        }

        SaleTransaction.Status newStatus = refundAmount.compareTo(saleTransaction.getTotalAmount()) == 0 
                ? SaleTransaction.Status.REFUNDED 
                : SaleTransaction.Status.PARTIALLY_REFUNDED;
        
        saleTransaction.setStatus(newStatus);
        saleTransaction.setNotes(saleTransaction.getNotes() + " [REFUNDED: " + refundAmount + " - " + reason + "]");

        SaleTransaction refundedTransaction = saleRepository.save(saleTransaction);
        return convertToSaleTransactionDTO(refundedTransaction);
    }

    // Helper Methods
    @Override
    public String generateSaleNumber(Long pumpId) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String pumpStr = String.format("%03d", pumpId);
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return "S" + pumpStr + dateStr + timeStr;
    }

    @Override
    public BigDecimal calculateTotalAmount(List<SaleItemDTO> saleItems) {
        return saleItems.stream()
                .map(SaleItemDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getSalesTotalsByPaymentMethod(Long shiftId) {
        Map<String, BigDecimal> totals = new HashMap<>();
        
        for (SaleTransaction.PaymentMethod method : SaleTransaction.PaymentMethod.values()) {
            Double amount = saleRepository.sumTotalAmountByShiftIdAndPaymentMethod(shiftId, method);
            totals.put(method.name(), BigDecimal.valueOf(amount != null ? amount : 0.0));
        }
        
        return totals;
    }

    @Override
    public Map<String, BigDecimal> getSalesTotalsByFuelType(Long shiftId) {
        // Implementation would require a custom query
        // For now, return empty map
        return new HashMap<>();
    }

    @Override
    public List<SaleTransactionDTO> getCreditSalesByCustomer(Long customerId) {
        List<SaleTransaction> sales = saleRepository.findCreditSalesByCustomer(customerId);
        return sales.stream()
                .map(this::convertToSaleTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getCustomerOutstandingBalance(Long customerId) {
        // Implementation would require calculating outstanding credit sales
        // For now, return zero
        return BigDecimal.ZERO;
    }

    // Conversion methods
    private SaleTransactionDTO convertToSaleTransactionDTO(SaleTransaction saleTransaction) {
        return SaleTransactionDTO.builder()
                .id(saleTransaction.getId())
                .pumpId(saleTransaction.getPumpId())
                .shiftId(saleTransaction.getShift() != null ? saleTransaction.getShift().getId() : null)
                .nozzleId(saleTransaction.getNozzle() != null ? saleTransaction.getNozzle().getId() : null)
                .fuelTypeId(saleTransaction.getFuelType() != null ? saleTransaction.getFuelType().getId() : null)
                .customerId(saleTransaction.getCustomer() != null ? saleTransaction.getCustomer().getId() : null)
                .saleNumber(saleTransaction.getSaleNumber())
                .quantity(saleTransaction.getQuantity())
                .rate(saleTransaction.getRate())
                .amount(saleTransaction.getAmount())
                .discountAmount(saleTransaction.getDiscountAmount())
                .taxAmount(saleTransaction.getTaxAmount())
                .totalAmount(saleTransaction.getTotalAmount())
                .paymentMethod(saleTransaction.getPaymentMethod())
                .saleType(saleTransaction.getSaleType())
                .status(saleTransaction.getStatus())
                .transactedAt(saleTransaction.getTransactedAt())
                .operatorId(saleTransaction.getOperatorId())
                .cashierId(saleTransaction.getCashierId())
                .vehicleNumber(saleTransaction.getVehicleNumber())
                .driverName(saleTransaction.getDriverName())
                .notes(saleTransaction.getNotes())
                .cardLastFour(saleTransaction.getCardLastFour())
                .cardType(saleTransaction.getCardType())
                .transactionReference(saleTransaction.getTransactionReference())
                .build();
    }

    // Placeholder implementations for remaining methods
    @Override
    public SaleItemDTO createSaleItem(SaleItemDTO saleItemDTO) {
        // Implementation needed
        return null;
    }

    @Override
    public SaleItemDTO updateSaleItem(Long id, SaleItemDTO saleItemDTO) {
        // Implementation needed
        return null;
    }

    @Override
    public SaleItemDTO getSaleItem(Long id) {
        // Implementation needed
        return null;
    }

    @Override
    public List<SaleItemDTO> getSaleItemsByTransaction(Long saleTransactionId) {
        // Implementation needed
        return null;
    }

    @Override
    public List<SaleItemDTO> getSaleItemsByShift(Long shiftId) {
        // Implementation needed
        return null;
    }

    @Override
    public void deleteSaleItem(Long id) {
        // Implementation needed
    }

    @Override
    public ShiftSalesSummaryDTO createShiftSalesSummary(ShiftSalesSummaryDTO summaryDTO) {
        // Implementation needed
        return null;
    }

    @Override
    public ShiftSalesSummaryDTO updateShiftSalesSummary(Long id, ShiftSalesSummaryDTO summaryDTO) {
        // Implementation needed
        return null;
    }

    @Override
    public ShiftSalesSummaryDTO getShiftSalesSummary(Long id) {
        // Implementation needed
        return null;
    }

    @Override
    public ShiftSalesSummaryDTO getShiftSalesSummaryByShift(Long shiftId) {
        // Implementation needed
        return null;
    }

    @Override
    public List<ShiftSalesSummaryDTO> getShiftSalesSummariesByPump(Long pumpId) {
        // Implementation needed
        return null;
    }

    @Override
    public List<ShiftSalesSummaryDTO> getShiftSalesSummariesByCashier(Long cashierId) {
        // Implementation needed
        return null;
    }

    @Override
    public void deleteShiftSalesSummary(Long id) {
        // Implementation needed
    }

    @Override
    public ShiftSalesSummaryDTO generateShiftSalesSummary(Long shiftId) {
        // Implementation needed
        return null;
    }

    @Override
    public ShiftSalesSummaryDTO approveShiftSalesSummary(Long id, Long approvedBy) {
        // Implementation needed
        return null;
    }

    @Override
    public ShiftSalesSummaryDTO submitShiftSalesSummary(Long id) {
        // Implementation needed
        return null;
    }

    @Override
    public Map<String, Object> getSalesDashboard(Long pumpId) {
        // Implementation needed
        return null;
    }

    @Override
    public Map<String, Object> getShiftSalesAnalytics(Long shiftId) {
        // Implementation needed
        return null;
    }

    @Override
    public Map<String, Object> getSalesSummaryByDateRange(Long pumpId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        return null;
    }

    @Override
    public Map<String, Object> getSalesByPaymentMethod(Long pumpId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        return null;
    }

    @Override
    public Map<String, Object> getSalesByFuelType(Long pumpId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation needed
        return null;
    }

    @Override
    public Map<String, Object> getSalesByOperatorReport(Long pumpId, LocalDateTime startDate, LocalDateTime endDate) {
        List<SaleTransactionDTO> sales = getSalesByOperator(pumpId, startDate, endDate);
        Map<String, Object> report = new HashMap<>();
        report.put("sales", sales);
        report.put("totalCount", sales.size());
        report.put("totalAmount", sales.stream()
                .map(SaleTransactionDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return report;
    }

    @Override
    public Map<String, Object> getSalesByCashierReport(Long pumpId, LocalDateTime startDate, LocalDateTime endDate) {
        List<SaleTransactionDTO> sales = getSalesByCashier(pumpId, startDate, endDate);
        Map<String, Object> report = new HashMap<>();
        report.put("sales", sales);
        report.put("totalCount", sales.size());
        report.put("totalAmount", sales.stream()
                .map(SaleTransactionDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return report;
    }

    @Override
    public SaleTransactionDTO createShiftSalesEntry(ShiftSalesEntryDTO entryDTO) {
        // Implementation needed
        return null;
    }

    @Override
    public SaleTransactionDTO createCashierSalesEntry(CashierSalesEntryDTO entryDTO) {
        // Implementation needed
        return null;
    }

    @Override
    public SaleTransactionDTO createCashCreditSalesEntry(CashCreditSalesEntryDTO entryDTO) {
        // Implementation needed
        return null;
    }

    @Override
    public SaleTransactionDTO createTankerCreditSalesEntry(TankerCreditSalesEntryDTO entryDTO) {
        // Implementation needed
        return null;
    }
}
