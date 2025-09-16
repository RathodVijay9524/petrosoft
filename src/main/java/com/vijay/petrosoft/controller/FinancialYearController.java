package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.FinancialYearDTO;
import com.vijay.petrosoft.service.FinancialYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/financial-years")
@RequiredArgsConstructor
public class FinancialYearController {

    private final FinancialYearService financialYearService;

    @PostMapping
    public ResponseEntity<FinancialYearDTO> createFinancialYear(@Valid @RequestBody FinancialYearDTO financialYearDTO) {
        FinancialYearDTO createdFinancialYear = financialYearService.createFinancialYear(financialYearDTO);
        return new ResponseEntity<>(createdFinancialYear, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialYearDTO> getFinancialYearById(@PathVariable Long id) {
        return financialYearService.getFinancialYearById(id)
                .map(financialYear -> new ResponseEntity<>(financialYear, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<FinancialYearDTO>> getAllFinancialYears() {
        List<FinancialYearDTO> financialYears = financialYearService.getAllFinancialYears();
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<FinancialYearDTO>> getFinancialYearsByPumpId(@PathVariable Long pumpId) {
        List<FinancialYearDTO> financialYears = financialYearService.getFinancialYearsByPumpId(pumpId);
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<FinancialYearDTO>> getActiveFinancialYears() {
        List<FinancialYearDTO> financialYears = financialYearService.getActiveFinancialYears();
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/active")
    public ResponseEntity<FinancialYearDTO> getActiveFinancialYearByPumpId(@PathVariable Long pumpId) {
        return financialYearService.getActiveFinancialYearByPumpId(pumpId)
                .map(financialYear -> new ResponseEntity<>(financialYear, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/global/active")
    public ResponseEntity<FinancialYearDTO> getActiveGlobalFinancialYear() {
        return financialYearService.getActiveGlobalFinancialYear()
                .map(financialYear -> new ResponseEntity<>(financialYear, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<FinancialYearDTO>> getFinancialYearsByDate(@PathVariable LocalDate date) {
        List<FinancialYearDTO> financialYears = financialYearService.getFinancialYearsByDate(date);
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/date/{date}")
    public ResponseEntity<FinancialYearDTO> getFinancialYearByPumpIdAndDate(
            @PathVariable Long pumpId, @PathVariable LocalDate date) {
        return financialYearService.getFinancialYearByPumpIdAndDate(pumpId, date)
                .map(financialYear -> new ResponseEntity<>(financialYear, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/future")
    public ResponseEntity<List<FinancialYearDTO>> getFutureFinancialYears() {
        List<FinancialYearDTO> financialYears = financialYearService.getFutureFinancialYears();
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @GetMapping("/past")
    public ResponseEntity<List<FinancialYearDTO>> getPastFinancialYears() {
        List<FinancialYearDTO> financialYears = financialYearService.getPastFinancialYears();
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @GetMapping("/global")
    public ResponseEntity<List<FinancialYearDTO>> getGlobalFinancialYears() {
        List<FinancialYearDTO> financialYears = financialYearService.getGlobalFinancialYears();
        return new ResponseEntity<>(financialYears, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialYearDTO> updateFinancialYear(@PathVariable Long id, @Valid @RequestBody FinancialYearDTO financialYearDTO) {
        FinancialYearDTO updatedFinancialYear = financialYearService.updateFinancialYear(id, financialYearDTO);
        return new ResponseEntity<>(updatedFinancialYear, HttpStatus.OK);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<FinancialYearDTO> activateFinancialYear(@PathVariable Long id) {
        FinancialYearDTO updatedFinancialYear = financialYearService.activateFinancialYear(id);
        return new ResponseEntity<>(updatedFinancialYear, HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<FinancialYearDTO> deactivateFinancialYear(@PathVariable Long id) {
        FinancialYearDTO updatedFinancialYear = financialYearService.deactivateFinancialYear(id);
        return new ResponseEntity<>(updatedFinancialYear, HttpStatus.OK);
    }

    @PutMapping("/{id}/set-active")
    public ResponseEntity<FinancialYearDTO> setActiveFinancialYear(@PathVariable Long id) {
        FinancialYearDTO updatedFinancialYear = financialYearService.setActiveFinancialYear(id);
        return new ResponseEntity<>(updatedFinancialYear, HttpStatus.OK);
    }

    @PostMapping("/pump/{pumpId}/create-default")
    public ResponseEntity<FinancialYearDTO> createDefaultFinancialYear(@PathVariable Long pumpId) {
        FinancialYearDTO createdFinancialYear = financialYearService.createDefaultFinancialYear(pumpId);
        return new ResponseEntity<>(createdFinancialYear, HttpStatus.CREATED);
    }

    @GetMapping("/pump/{pumpId}/current")
    public ResponseEntity<FinancialYearDTO> getCurrentFinancialYear(@PathVariable Long pumpId) {
        FinancialYearDTO currentFinancialYear = financialYearService.getCurrentFinancialYear(pumpId);
        return new ResponseEntity<>(currentFinancialYear, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/date/{date}/in-current-year")
    public ResponseEntity<Boolean> isDateInCurrentFinancialYear(@PathVariable Long pumpId, @PathVariable LocalDate date) {
        boolean isInCurrentYear = financialYearService.isDateInCurrentFinancialYear(pumpId, date);
        return new ResponseEntity<>(isInCurrentYear, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialYear(@PathVariable Long id) {
        financialYearService.deleteFinancialYear(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
