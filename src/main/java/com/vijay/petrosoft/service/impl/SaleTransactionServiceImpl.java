package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.SaleTransaction;
import com.vijay.petrosoft.dto.SaleTransactionDTO;
import com.vijay.petrosoft.repository.SaleRepository;
import com.vijay.petrosoft.service.SaleTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleTransactionServiceImpl implements SaleTransactionService {

    private final SaleRepository saleRepository;

    @Override
    public SaleTransactionDTO createSale(SaleTransactionDTO saleDTO) {
        SaleTransaction sale = SaleTransaction.builder()
                .pumpId(saleDTO.getPumpId())
                .quantity(saleDTO.getQuantity())
                .rate(saleDTO.getRate())
                .amount(saleDTO.getAmount())
                .creditSale(saleDTO.isCreditSale())
                .customerId(saleDTO.getCustomerId())
                .transactedAt(saleDTO.getTransactedAt() != null ? saleDTO.getTransactedAt() : LocalDateTime.now())
                .build();

        SaleTransaction savedSale = saleRepository.save(sale);
        return convertToDTO(savedSale);
    }

    @Override
    public SaleTransactionDTO updateSale(Long id, SaleTransactionDTO saleDTO) {
        SaleTransaction sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale transaction not found with id: " + id));

        sale.setPumpId(saleDTO.getPumpId());
        sale.setQuantity(saleDTO.getQuantity());
        sale.setRate(saleDTO.getRate());
        sale.setAmount(saleDTO.getAmount());
        sale.setCreditSale(saleDTO.isCreditSale());
        sale.setCustomerId(saleDTO.getCustomerId());
        sale.setTransactedAt(saleDTO.getTransactedAt());

        SaleTransaction updatedSale = saleRepository.save(sale);
        return convertToDTO(updatedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SaleTransactionDTO> getSaleById(Long id) {
        return saleRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTransactionDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSale(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new RuntimeException("Sale transaction not found with id: " + id);
        }
        saleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTransactionDTO> getSalesByPumpId(Long pumpId) {
        return saleRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTransactionDTO> getSalesByShiftId(Long shiftId) {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getShift() != null && sale.getShift().getId().equals(shiftId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTransactionDTO> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return saleRepository.findByTransactedAtBetween(startDateTime, endDateTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTransactionDTO> getSalesByCustomerId(Long customerId) {
        return saleRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTransactionDTO> getCreditSales() {
        return saleRepository.findByCreditSaleTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleTransactionDTO> getCashSales() {
        return saleRepository.findByCreditSaleFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSalesByDate(LocalDate date) {
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(23, 59, 59);
        return saleRepository.findByTransactedAtBetween(startDateTime, endDateTime).stream()
                .map(SaleTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSalesByShift(Long shiftId) {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getShift() != null && sale.getShift().getId().equals(shiftId))
                .map(SaleTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public SaleTransactionDTO processFuelSale(Long nozzleId, BigDecimal quantity, Long customerId, boolean isCredit) {
        // This method would integrate with actual fuel dispensing system
        // For now, we'll create a basic sale transaction
        SaleTransactionDTO saleDTO = SaleTransactionDTO.builder()
                .nozzleId(nozzleId)
                .quantity(quantity)
                .customerId(customerId)
                .creditSale(isCredit)
                .transactedAt(LocalDateTime.now())
                .build();

        return createSale(saleDTO);
    }

    private SaleTransactionDTO convertToDTO(SaleTransaction sale) {
        return SaleTransactionDTO.builder()
                .id(sale.getId())
                .pumpId(sale.getPumpId())
                .shiftId(sale.getShift() != null ? sale.getShift().getId() : null)
                .nozzleId(sale.getNozzle() != null ? sale.getNozzle().getId() : null)
                .fuelTypeId(sale.getFuelType() != null ? sale.getFuelType().getId() : null)
                .quantity(sale.getQuantity())
                .rate(sale.getRate())
                .amount(sale.getAmount())
                .creditSale(sale.isCreditSale())
                .customerId(sale.getCustomerId())
                .transactedAt(sale.getTransactedAt())
                .build();
    }
}
