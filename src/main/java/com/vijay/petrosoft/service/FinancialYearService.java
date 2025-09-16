package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.FinancialYearDTO;
import com.vijay.petrosoft.domain.FinancialYear;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialYearService {
    
    // CRUD Operations
    FinancialYearDTO createFinancialYear(FinancialYearDTO financialYearDTO);
    FinancialYearDTO updateFinancialYear(Long id, FinancialYearDTO financialYearDTO);
    Optional<FinancialYearDTO> getFinancialYearById(Long id);
    List<FinancialYearDTO> getAllFinancialYears();
    List<FinancialYearDTO> getFinancialYearsByPumpId(Long pumpId);
    void deleteFinancialYear(Long id);
    
    // Active Financial Year Management
    List<FinancialYearDTO> getActiveFinancialYears();
    Optional<FinancialYearDTO> getActiveFinancialYearByPumpId(Long pumpId);
    Optional<FinancialYearDTO> getActiveGlobalFinancialYear();
    FinancialYearDTO setActiveFinancialYear(Long id);
    
    // Date-based Queries
    List<FinancialYearDTO> getFinancialYearsByDate(LocalDate date);
    Optional<FinancialYearDTO> getFinancialYearByPumpIdAndDate(Long pumpId, LocalDate date);
    List<FinancialYearDTO> getFutureFinancialYears();
    List<FinancialYearDTO> getPastFinancialYears();
    
    // Business Logic
    FinancialYearDTO activateFinancialYear(Long id);
    FinancialYearDTO deactivateFinancialYear(Long id);
    boolean isDateInCurrentFinancialYear(Long pumpId, LocalDate date);
    FinancialYearDTO getCurrentFinancialYear(Long pumpId);
    
    // Utility Methods
    FinancialYearDTO createDefaultFinancialYear(Long pumpId);
    List<FinancialYearDTO> getGlobalFinancialYears();
    boolean validateFinancialYearDates(LocalDate startDate, LocalDate endDate);
}
