package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.*;
import com.vijay.petrosoft.service.CashManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cash-management")
@RequiredArgsConstructor
public class CashManagementController {

    private final CashManagementService cashManagementService;

    // Cash Movement Endpoints
    @PostMapping("/movements")
    public ResponseEntity<CashMovementDTO> createCashMovement(@Valid @RequestBody CashMovementDTO cashMovementDTO) {
        CashMovementDTO createdMovement = cashManagementService.createCashMovement(cashMovementDTO);
        return new ResponseEntity<>(createdMovement, HttpStatus.CREATED);
    }

    @PutMapping("/movements/{id}")
    public ResponseEntity<CashMovementDTO> updateCashMovement(@PathVariable Long id, @Valid @RequestBody CashMovementDTO cashMovementDTO) {
        CashMovementDTO updatedMovement = cashManagementService.updateCashMovement(id, cashMovementDTO);
        return ResponseEntity.ok(updatedMovement);
    }

    @GetMapping("/movements/{id}")
    public ResponseEntity<CashMovementDTO> getCashMovement(@PathVariable Long id) {
        CashMovementDTO movement = cashManagementService.getCashMovement(id);
        return ResponseEntity.ok(movement);
    }

    @GetMapping("/movements/pump/{pumpId}")
    public ResponseEntity<List<CashMovementDTO>> getCashMovementsByPump(@PathVariable Long pumpId) {
        List<CashMovementDTO> movements = cashManagementService.getCashMovementsByPump(pumpId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/movements/shift/{shiftId}")
    public ResponseEntity<List<CashMovementDTO>> getCashMovementsByShift(@PathVariable Long shiftId) {
        List<CashMovementDTO> movements = cashManagementService.getCashMovementsByShift(shiftId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/movements/pump/{pumpId}/date-range")
    public ResponseEntity<List<CashMovementDTO>> getCashMovementsByDateRange(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<CashMovementDTO> movements = cashManagementService.getCashMovementsByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(movements);
    }

    @DeleteMapping("/movements/{id}")
    public ResponseEntity<Void> deleteCashMovement(@PathVariable Long id) {
        cashManagementService.deleteCashMovement(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/movements/{id}/approve")
    public ResponseEntity<CashMovementDTO> approveCashMovement(@PathVariable Long id, @RequestParam Long approvedBy) {
        CashMovementDTO approvedMovement = cashManagementService.approveCashMovement(id, approvedBy);
        return ResponseEntity.ok(approvedMovement);
    }

    @PostMapping("/movements/{id}/reject")
    public ResponseEntity<CashMovementDTO> rejectCashMovement(@PathVariable Long id, @RequestParam String reason) {
        CashMovementDTO rejectedMovement = cashManagementService.rejectCashMovement(id, reason);
        return ResponseEntity.ok(rejectedMovement);
    }

    // Currency Denomination Endpoints
    @PostMapping("/denominations")
    public ResponseEntity<CurrencyDenominationDTO> createCurrencyDenomination(@Valid @RequestBody CurrencyDenominationDTO denominationDTO) {
        CurrencyDenominationDTO createdDenomination = cashManagementService.createCurrencyDenomination(denominationDTO);
        return new ResponseEntity<>(createdDenomination, HttpStatus.CREATED);
    }

    @PutMapping("/denominations/{id}")
    public ResponseEntity<CurrencyDenominationDTO> updateCurrencyDenomination(@PathVariable Long id, @Valid @RequestBody CurrencyDenominationDTO denominationDTO) {
        CurrencyDenominationDTO updatedDenomination = cashManagementService.updateCurrencyDenomination(id, denominationDTO);
        return ResponseEntity.ok(updatedDenomination);
    }

    @GetMapping("/denominations/{id}")
    public ResponseEntity<CurrencyDenominationDTO> getCurrencyDenomination(@PathVariable Long id) {
        CurrencyDenominationDTO denomination = cashManagementService.getCurrencyDenomination(id);
        return ResponseEntity.ok(denomination);
    }

    @GetMapping("/denominations/shift/{shiftId}")
    public ResponseEntity<List<CurrencyDenominationDTO>> getCurrencyDenominationsByShift(@PathVariable Long shiftId) {
        List<CurrencyDenominationDTO> denominations = cashManagementService.getCurrencyDenominationsByShift(shiftId);
        return ResponseEntity.ok(denominations);
    }

    @GetMapping("/denominations/pump/{pumpId}")
    public ResponseEntity<List<CurrencyDenominationDTO>> getCurrencyDenominationsByPump(@PathVariable Long pumpId) {
        List<CurrencyDenominationDTO> denominations = cashManagementService.getCurrencyDenominationsByPump(pumpId);
        return ResponseEntity.ok(denominations);
    }

    @DeleteMapping("/denominations/{id}")
    public ResponseEntity<Void> deleteCurrencyDenomination(@PathVariable Long id) {
        cashManagementService.deleteCurrencyDenomination(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/denominations/{id}/verify")
    public ResponseEntity<CurrencyDenominationDTO> verifyCurrencyDenomination(@PathVariable Long id, @RequestParam Long verifiedBy) {
        CurrencyDenominationDTO verifiedDenomination = cashManagementService.verifyCurrencyDenomination(id, verifiedBy);
        return ResponseEntity.ok(verifiedDenomination);
    }

    @GetMapping("/denominations/shift/{shiftId}/summary")
    public ResponseEntity<Map<String, java.math.BigDecimal>> getCurrencySummaryByShift(@PathVariable Long shiftId) {
        Map<String, java.math.BigDecimal> summary = cashManagementService.getCurrencySummaryByShift(shiftId);
        return ResponseEntity.ok(summary);
    }

    // Cash Collection Endpoints
    @PostMapping("/collections")
    public ResponseEntity<CashCollectionDTO> createCashCollection(@Valid @RequestBody CashCollectionDTO collectionDTO) {
        CashCollectionDTO createdCollection = cashManagementService.createCashCollection(collectionDTO);
        return new ResponseEntity<>(createdCollection, HttpStatus.CREATED);
    }

    @PutMapping("/collections/{id}")
    public ResponseEntity<CashCollectionDTO> updateCashCollection(@PathVariable Long id, @Valid @RequestBody CashCollectionDTO collectionDTO) {
        CashCollectionDTO updatedCollection = cashManagementService.updateCashCollection(id, collectionDTO);
        return ResponseEntity.ok(updatedCollection);
    }

    @GetMapping("/collections/{id}")
    public ResponseEntity<CashCollectionDTO> getCashCollection(@PathVariable Long id) {
        CashCollectionDTO collection = cashManagementService.getCashCollection(id);
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/collections/shift/{shiftId}")
    public ResponseEntity<List<CashCollectionDTO>> getCashCollectionsByShift(@PathVariable Long shiftId) {
        List<CashCollectionDTO> collections = cashManagementService.getCashCollectionsByShift(shiftId);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/collections/pump/{pumpId}")
    public ResponseEntity<List<CashCollectionDTO>> getCashCollectionsByPump(@PathVariable Long pumpId) {
        List<CashCollectionDTO> collections = cashManagementService.getCashCollectionsByPump(pumpId);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/collections/cashier/{cashierId}")
    public ResponseEntity<List<CashCollectionDTO>> getCashCollectionsByCashier(@PathVariable Long cashierId) {
        List<CashCollectionDTO> collections = cashManagementService.getCashCollectionsByCashier(cashierId);
        return ResponseEntity.ok(collections);
    }

    @DeleteMapping("/collections/{id}")
    public ResponseEntity<Void> deleteCashCollection(@PathVariable Long id) {
        cashManagementService.deleteCashCollection(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/collections/{id}/submit")
    public ResponseEntity<CashCollectionDTO> submitCashCollection(@PathVariable Long id) {
        CashCollectionDTO submittedCollection = cashManagementService.submitCashCollection(id);
        return ResponseEntity.ok(submittedCollection);
    }

    @PostMapping("/collections/{id}/verify")
    public ResponseEntity<CashCollectionDTO> verifyCashCollection(@PathVariable Long id, @RequestParam Long verifiedBy) {
        CashCollectionDTO verifiedCollection = cashManagementService.verifyCashCollection(id, verifiedBy);
        return ResponseEntity.ok(verifiedCollection);
    }

    @PostMapping("/collections/{id}/approve")
    public ResponseEntity<CashCollectionDTO> approveCashCollection(@PathVariable Long id, @RequestParam Long approvedBy) {
        CashCollectionDTO approvedCollection = cashManagementService.approveCashCollection(id, approvedBy);
        return ResponseEntity.ok(approvedCollection);
    }

    @PostMapping("/collections/calculate/{shiftId}")
    public ResponseEntity<CashCollectionDTO> calculateCashCollection(@PathVariable Long shiftId) {
        CashCollectionDTO calculatedCollection = cashManagementService.calculateCashCollection(shiftId);
        return ResponseEntity.ok(calculatedCollection);
    }

    // Shift Management Endpoints
    @PostMapping("/shifts")
    public ResponseEntity<ShiftDTO> createShift(@Valid @RequestBody ShiftDTO shiftDTO) {
        ShiftDTO createdShift = cashManagementService.createShift(shiftDTO);
        return new ResponseEntity<>(createdShift, HttpStatus.CREATED);
    }

    @PutMapping("/shifts/{id}")
    public ResponseEntity<ShiftDTO> updateShift(@PathVariable Long id, @Valid @RequestBody ShiftDTO shiftDTO) {
        ShiftDTO updatedShift = cashManagementService.updateShift(id, shiftDTO);
        return ResponseEntity.ok(updatedShift);
    }

    @GetMapping("/shifts/{id}")
    public ResponseEntity<ShiftDTO> getShift(@PathVariable Long id) {
        ShiftDTO shift = cashManagementService.getShift(id);
        return ResponseEntity.ok(shift);
    }

    @GetMapping("/shifts/pump/{pumpId}")
    public ResponseEntity<List<ShiftDTO>> getShiftsByPump(@PathVariable Long pumpId) {
        List<ShiftDTO> shifts = cashManagementService.getShiftsByPump(pumpId);
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/shifts/pump/{pumpId}/active")
    public ResponseEntity<List<ShiftDTO>> getActiveShiftsByPump(@PathVariable Long pumpId) {
        List<ShiftDTO> shifts = cashManagementService.getActiveShiftsByPump(pumpId);
        return ResponseEntity.ok(shifts);
    }

    @PostMapping("/shifts/open")
    public ResponseEntity<ShiftDTO> openShift(@Valid @RequestBody ShiftDTO shiftDTO) {
        ShiftDTO openedShift = cashManagementService.openShift(shiftDTO);
        return new ResponseEntity<>(openedShift, HttpStatus.CREATED);
    }

    @PostMapping("/shifts/{id}/close")
    public ResponseEntity<ShiftDTO> closeShift(@PathVariable Long id, @RequestParam Long closedBy, @RequestParam(required = false) String notes) {
        ShiftDTO closedShift = cashManagementService.closeShift(id, closedBy, notes);
        return ResponseEntity.ok(closedShift);
    }

    @GetMapping("/shifts/pump/{pumpId}/current")
    public ResponseEntity<ShiftDTO> getCurrentShiftByPump(@PathVariable Long pumpId) {
        ShiftDTO currentShift = cashManagementService.getCurrentShiftByPump(pumpId);
        return ResponseEntity.ok(currentShift);
    }

    @GetMapping("/shifts/pump/{pumpId}/date-range")
    public ResponseEntity<List<ShiftDTO>> getShiftsByDateRange(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<ShiftDTO> shifts = cashManagementService.getShiftsByDateRange(pumpId, startDate, endDate);
        return ResponseEntity.ok(shifts);
    }

    // Dashboard and Reports
    @GetMapping("/dashboard/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getCashManagementDashboard(@PathVariable Long pumpId) {
        Map<String, Object> dashboard = cashManagementService.getCashManagementDashboard(pumpId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/summary/shift/{shiftId}")
    public ResponseEntity<Map<String, Object>> getShiftCashSummary(@PathVariable Long shiftId) {
        Map<String, Object> summary = cashManagementService.getShiftCashSummary(shiftId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reports/cash-flow/pump/{pumpId}")
    public ResponseEntity<Map<String, Object>> getCashFlowReport(
            @PathVariable Long pumpId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Map<String, Object> report = cashManagementService.getCashFlowReport(pumpId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
