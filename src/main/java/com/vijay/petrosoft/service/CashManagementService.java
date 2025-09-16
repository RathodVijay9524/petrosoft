package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.*;
import com.vijay.petrosoft.domain.CashMovement;
import com.vijay.petrosoft.domain.CurrencyDenomination;
import com.vijay.petrosoft.domain.CashCollection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CashManagementService {

    // Cash Movement Operations
    CashMovementDTO createCashMovement(CashMovementDTO cashMovementDTO);
    CashMovementDTO updateCashMovement(Long id, CashMovementDTO cashMovementDTO);
    CashMovementDTO getCashMovement(Long id);
    List<CashMovementDTO> getCashMovementsByPump(Long pumpId);
    List<CashMovementDTO> getCashMovementsByShift(Long shiftId);
    List<CashMovementDTO> getCashMovementsByDateRange(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteCashMovement(Long id);
    CashMovementDTO approveCashMovement(Long id, Long approvedBy);
    CashMovementDTO rejectCashMovement(Long id, String reason);

    // Currency Denomination Operations
    CurrencyDenominationDTO createCurrencyDenomination(CurrencyDenominationDTO denominationDTO);
    CurrencyDenominationDTO updateCurrencyDenomination(Long id, CurrencyDenominationDTO denominationDTO);
    CurrencyDenominationDTO getCurrencyDenomination(Long id);
    List<CurrencyDenominationDTO> getCurrencyDenominationsByShift(Long shiftId);
    List<CurrencyDenominationDTO> getCurrencyDenominationsByPump(Long pumpId);
    void deleteCurrencyDenomination(Long id);
    CurrencyDenominationDTO verifyCurrencyDenomination(Long id, Long verifiedBy);
    Map<String, BigDecimal> getCurrencySummaryByShift(Long shiftId);

    // Cash Collection Operations
    CashCollectionDTO createCashCollection(CashCollectionDTO collectionDTO);
    CashCollectionDTO updateCashCollection(Long id, CashCollectionDTO collectionDTO);
    CashCollectionDTO getCashCollection(Long id);
    List<CashCollectionDTO> getCashCollectionsByShift(Long shiftId);
    List<CashCollectionDTO> getCashCollectionsByPump(Long pumpId);
    List<CashCollectionDTO> getCashCollectionsByCashier(Long cashierId);
    void deleteCashCollection(Long id);
    CashCollectionDTO submitCashCollection(Long id);
    CashCollectionDTO verifyCashCollection(Long id, Long verifiedBy);
    CashCollectionDTO approveCashCollection(Long id, Long approvedBy);
    CashCollectionDTO calculateCashCollection(Long shiftId);

    // Shift Management Operations
    ShiftDTO createShift(ShiftDTO shiftDTO);
    ShiftDTO updateShift(Long id, ShiftDTO shiftDTO);
    ShiftDTO getShift(Long id);
    List<ShiftDTO> getShiftsByPump(Long pumpId);
    List<ShiftDTO> getActiveShiftsByPump(Long pumpId);
    ShiftDTO openShift(ShiftDTO shiftDTO);
    ShiftDTO closeShift(Long id, Long closedBy, String notes);
    ShiftDTO getCurrentShiftByPump(Long pumpId);
    List<ShiftDTO> getShiftsByDateRange(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);

    // Cash Management Dashboard
    Map<String, Object> getCashManagementDashboard(Long pumpId);
    Map<String, Object> getShiftCashSummary(Long shiftId);
    Map<String, Object> getCashFlowReport(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);

    // Helper Methods
    BigDecimal calculateTotalCashByDenominations(Long shiftId);
    BigDecimal calculateNotesTotal(Long shiftId);
    BigDecimal calculateCoinsTotal(Long shiftId);
    Map<String, BigDecimal> getCashBalanceByShift(Long shiftId);
}
