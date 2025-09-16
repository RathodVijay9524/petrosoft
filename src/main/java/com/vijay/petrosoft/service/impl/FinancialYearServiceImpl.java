package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.FinancialYear;
import com.vijay.petrosoft.dto.FinancialYearDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.BusinessLogicException;
import com.vijay.petrosoft.repository.FinancialYearRepository;
import com.vijay.petrosoft.service.FinancialYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialYearServiceImpl implements FinancialYearService {

    private final FinancialYearRepository financialYearRepository;

    @Override
    public FinancialYearDTO createFinancialYear(FinancialYearDTO financialYearDTO) {
        // Validate dates
        if (!validateFinancialYearDates(financialYearDTO.getStartDate(), financialYearDTO.getEndDate())) {
            throw new BusinessLogicException("End date must be after start date");
        }

        // Check for duplicate name
        if (financialYearRepository.existsByNameAndPumpId(financialYearDTO.getName(), financialYearDTO.getPumpId())) {
            throw new DuplicateResourceException("Financial year with this name already exists for the pump");
        }

        // If setting as active, deactivate other financial years for the same pump
        if (financialYearDTO.isActive()) {
            deactivateOtherFinancialYears(financialYearDTO.getPumpId());
        }

        FinancialYear financialYear = FinancialYear.builder()
                .name(financialYearDTO.getName())
                .startDate(financialYearDTO.getStartDate())
                .endDate(financialYearDTO.getEndDate())
                .active(financialYearDTO.isActive())
                .description(financialYearDTO.getDescription())
                .pumpId(financialYearDTO.getPumpId())
                .build();

        FinancialYear savedFinancialYear = financialYearRepository.save(financialYear);
        return convertToDTO(savedFinancialYear);
    }

    @Override
    public FinancialYearDTO updateFinancialYear(Long id, FinancialYearDTO financialYearDTO) {
        FinancialYear financialYear = financialYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial year not found with id: " + id));

        // Validate dates
        if (!validateFinancialYearDates(financialYearDTO.getStartDate(), financialYearDTO.getEndDate())) {
            throw new BusinessLogicException("End date must be after start date");
        }

        // If setting as active, deactivate other financial years for the same pump
        if (financialYearDTO.isActive() && !financialYear.isActive()) {
            deactivateOtherFinancialYears(financialYearDTO.getPumpId());
        }

        financialYear.setName(financialYearDTO.getName());
        financialYear.setStartDate(financialYearDTO.getStartDate());
        financialYear.setEndDate(financialYearDTO.getEndDate());
        financialYear.setActive(financialYearDTO.isActive());
        financialYear.setDescription(financialYearDTO.getDescription());
        financialYear.setPumpId(financialYearDTO.getPumpId());

        FinancialYear updatedFinancialYear = financialYearRepository.save(financialYear);
        return convertToDTO(updatedFinancialYear);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialYearDTO> getFinancialYearById(Long id) {
        return financialYearRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialYearDTO> getAllFinancialYears() {
        return financialYearRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialYearDTO> getFinancialYearsByPumpId(Long pumpId) {
        return financialYearRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFinancialYear(Long id) {
        if (!financialYearRepository.existsById(id)) {
            throw new ResourceNotFoundException("Financial year not found with id: " + id);
        }
        financialYearRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialYearDTO> getActiveFinancialYears() {
        return financialYearRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialYearDTO> getActiveFinancialYearByPumpId(Long pumpId) {
        return financialYearRepository.findActiveFinancialYearByPumpId(pumpId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialYearDTO> getActiveGlobalFinancialYear() {
        return financialYearRepository.findActiveGlobalFinancialYear()
                .map(this::convertToDTO);
    }

    @Override
    public FinancialYearDTO setActiveFinancialYear(Long id) {
        FinancialYear financialYear = financialYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial year not found with id: " + id));

        // Deactivate other financial years for the same pump
        deactivateOtherFinancialYears(financialYear.getPumpId());

        financialYear.setActive(true);
        FinancialYear updatedFinancialYear = financialYearRepository.save(financialYear);
        return convertToDTO(updatedFinancialYear);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialYearDTO> getFinancialYearsByDate(LocalDate date) {
        return financialYearRepository.findFinancialYearsByDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialYearDTO> getFinancialYearByPumpIdAndDate(Long pumpId, LocalDate date) {
        return financialYearRepository.findFinancialYearByPumpIdAndDate(pumpId, date)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialYearDTO> getFutureFinancialYears() {
        return financialYearRepository.findFutureFinancialYears(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialYearDTO> getPastFinancialYears() {
        return financialYearRepository.findPastFinancialYears(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FinancialYearDTO activateFinancialYear(Long id) {
        return setActiveFinancialYear(id);
    }

    @Override
    public FinancialYearDTO deactivateFinancialYear(Long id) {
        FinancialYear financialYear = financialYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial year not found with id: " + id));

        financialYear.setActive(false);
        FinancialYear updatedFinancialYear = financialYearRepository.save(financialYear);
        return convertToDTO(updatedFinancialYear);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDateInCurrentFinancialYear(Long pumpId, LocalDate date) {
        Optional<FinancialYear> activeFY = financialYearRepository.findActiveFinancialYearByPumpId(pumpId);
        if (activeFY.isEmpty()) {
            return false;
        }
        FinancialYear fy = activeFY.get();
        return !date.isBefore(fy.getStartDate()) && !date.isAfter(fy.getEndDate());
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialYearDTO getCurrentFinancialYear(Long pumpId) {
        return getActiveFinancialYearByPumpId(pumpId)
                .orElseThrow(() -> new ResourceNotFoundException("No active financial year found for pump: " + pumpId));
    }

    @Override
    public FinancialYearDTO createDefaultFinancialYear(Long pumpId) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        
        // Create default financial year (April to March)
        LocalDate startDate = LocalDate.of(currentYear, 4, 1);
        LocalDate endDate = LocalDate.of(currentYear + 1, 3, 31);
        
        // If current date is before April, use previous year's financial year
        if (now.getMonthValue() < 4) {
            startDate = LocalDate.of(currentYear - 1, 4, 1);
            endDate = LocalDate.of(currentYear, 3, 31);
        }

        String name = "FY " + startDate.getYear() + "-" + (startDate.getYear() + 1);

        FinancialYearDTO financialYearDTO = FinancialYearDTO.builder()
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .active(true)
                .description("Default financial year created automatically")
                .pumpId(pumpId)
                .build();

        return createFinancialYear(financialYearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialYearDTO> getGlobalFinancialYears() {
        return financialYearRepository.findGlobalFinancialYears().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateFinancialYearDates(LocalDate startDate, LocalDate endDate) {
        return endDate.isAfter(startDate);
    }

    private void deactivateOtherFinancialYears(Long pumpId) {
        List<FinancialYear> activeFinancialYears = financialYearRepository.findByActiveTrue();
        for (FinancialYear fy : activeFinancialYears) {
            if (fy.getPumpId() != null && fy.getPumpId().equals(pumpId)) {
                fy.setActive(false);
                financialYearRepository.save(fy);
            } else if (fy.getPumpId() == null && pumpId == null) {
                fy.setActive(false);
                financialYearRepository.save(fy);
            }
        }
    }

    private FinancialYearDTO convertToDTO(FinancialYear financialYear) {
        return FinancialYearDTO.builder()
                .id(financialYear.getId())
                .name(financialYear.getName())
                .startDate(financialYear.getStartDate())
                .endDate(financialYear.getEndDate())
                .active(financialYear.isActive())
                .description(financialYear.getDescription())
                .pumpId(financialYear.getPumpId())
                .isCurrentYear(financialYear.isCurrentYear())
                .isPastYear(financialYear.isPastYear())
                .isFutureYear(financialYear.isFutureYear())
                .build();
    }
}
