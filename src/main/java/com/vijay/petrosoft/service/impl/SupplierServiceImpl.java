package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Supplier;
import com.vijay.petrosoft.dto.SupplierDTO;
import com.vijay.petrosoft.exception.ResourceNotFoundException;
import com.vijay.petrosoft.repository.SupplierRepository;
import com.vijay.petrosoft.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SupplierServiceImpl implements SupplierService {
    
    private final SupplierRepository supplierRepository;
    
    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        log.info("Creating supplier: {}", supplierDTO.getSupplierName());
        
        // Validate uniqueness
        if (!isSupplierCodeUnique(supplierDTO.getSupplierCode(), null)) {
            throw new RuntimeException("Supplier code already exists: " + supplierDTO.getSupplierCode());
        }
        
        if (supplierDTO.getEmail() != null && !isEmailUnique(supplierDTO.getEmail(), null)) {
            throw new RuntimeException("Email already exists: " + supplierDTO.getEmail());
        }
        
        if (supplierDTO.getGstNumber() != null && !isGstNumberUnique(supplierDTO.getGstNumber(), null)) {
            throw new RuntimeException("GST number already exists: " + supplierDTO.getGstNumber());
        }
        
        if (supplierDTO.getPanNumber() != null && !isPanNumberUnique(supplierDTO.getPanNumber(), null)) {
            throw new RuntimeException("PAN number already exists: " + supplierDTO.getPanNumber());
        }
        
        Supplier supplier = convertToEntity(supplierDTO);
        supplier.setCreatedAt(LocalDateTime.now());
        
        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Supplier created successfully with ID: {}", savedSupplier.getId());
        
        return convertToDTO(savedSupplier);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        log.info("Getting supplier by ID: {}", id);
        
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        return convertToDTO(supplier);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierByCode(String supplierCode) {
        log.info("Getting supplier by code: {}", supplierCode);
        
        Supplier supplier = supplierRepository.findBySupplierCode(supplierCode)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with code: " + supplierCode));
        
        return convertToDTO(supplier);
    }
    
    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        log.info("Updating supplier with ID: {}", id);
        
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        // Validate uniqueness for updated values
        if (!supplierDTO.getSupplierCode().equals(existingSupplier.getSupplierCode()) && 
            !isSupplierCodeUnique(supplierDTO.getSupplierCode(), id)) {
            throw new RuntimeException("Supplier code already exists: " + supplierDTO.getSupplierCode());
        }
        
        if (supplierDTO.getEmail() != null && !supplierDTO.getEmail().equals(existingSupplier.getEmail()) && 
            !isEmailUnique(supplierDTO.getEmail(), id)) {
            throw new RuntimeException("Email already exists: " + supplierDTO.getEmail());
        }
        
        if (supplierDTO.getGstNumber() != null && !supplierDTO.getGstNumber().equals(existingSupplier.getGstNumber()) && 
            !isGstNumberUnique(supplierDTO.getGstNumber(), id)) {
            throw new RuntimeException("GST number already exists: " + supplierDTO.getGstNumber());
        }
        
        if (supplierDTO.getPanNumber() != null && !supplierDTO.getPanNumber().equals(existingSupplier.getPanNumber()) && 
            !isPanNumberUnique(supplierDTO.getPanNumber(), id)) {
            throw new RuntimeException("PAN number already exists: " + supplierDTO.getPanNumber());
        }
        
        // Update fields
        updateSupplierFields(existingSupplier, supplierDTO);
        existingSupplier.setUpdatedAt(LocalDateTime.now());
        
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        log.info("Supplier updated successfully with ID: {}", updatedSupplier.getId());
        
        return convertToDTO(updatedSupplier);
    }
    
    @Override
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with ID: {}", id);
        
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        
        supplierRepository.deleteById(id);
        log.info("Supplier deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersByPump(Long pumpId) {
        log.info("Getting suppliers by pump ID: {}", pumpId);
        
        List<Supplier> suppliers = supplierRepository.findByPumpIdOrderBySupplierNameAsc(pumpId);
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getSuppliersByPump(Long pumpId, Pageable pageable) {
        log.info("Getting suppliers by pump ID: {} with pagination", pumpId);
        
        Page<Supplier> suppliers = supplierRepository.findByPumpIdOrderBySupplierNameAsc(pumpId, pageable);
        return suppliers.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersByStatus(Supplier.Status status) {
        log.info("Getting suppliers by status: {}", status);
        
        List<Supplier> suppliers = supplierRepository.findByStatusOrderBySupplierNameAsc(status);
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersByCity(String city) {
        log.info("Getting suppliers by city: {}", city);
        
        List<Supplier> suppliers = supplierRepository.findByCityOrderBySupplierNameAsc(city);
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersByState(String state) {
        log.info("Getting suppliers by state: {}", state);
        
        List<Supplier> suppliers = supplierRepository.findByStateOrderBySupplierNameAsc(state);
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> searchSuppliers(Long pumpId, String searchTerm) {
        log.info("Searching suppliers by term: {} for pump: {}", searchTerm, pumpId);
        
        List<Supplier> suppliers = supplierRepository.searchSuppliers(pumpId, searchTerm);
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersWithCreditLimit(Long pumpId) {
        log.info("Getting suppliers with credit limit for pump: {}", pumpId);
        
        List<Supplier> suppliers = supplierRepository.findSuppliersWithCreditLimit(pumpId);
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliersByPaymentTerms(Long pumpId, String paymentTerms) {
        log.info("Getting suppliers by payment terms: {} for pump: {}", paymentTerms, pumpId);
        
        List<Supplier> suppliers = supplierRepository.findByPaymentTermsContainingIgnoreCaseAndPumpIdOrderBySupplierNameAsc(paymentTerms, pumpId);
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public SupplierDTO activateSupplier(Long id) {
        log.info("Activating supplier with ID: {}", id);
        
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        supplier.setStatus(Supplier.Status.ACTIVE);
        supplier.setUpdatedAt(LocalDateTime.now());
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier activated successfully with ID: {}", updatedSupplier.getId());
        
        return convertToDTO(updatedSupplier);
    }
    
    @Override
    public SupplierDTO deactivateSupplier(Long id) {
        log.info("Deactivating supplier with ID: {}", id);
        
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        supplier.setStatus(Supplier.Status.INACTIVE);
        supplier.setUpdatedAt(LocalDateTime.now());
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier deactivated successfully with ID: {}", updatedSupplier.getId());
        
        return convertToDTO(updatedSupplier);
    }
    
    @Override
    public SupplierDTO suspendSupplier(Long id, String reason) {
        log.info("Suspending supplier with ID: {} for reason: {}", id, reason);
        
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        supplier.setStatus(Supplier.Status.SUSPENDED);
        supplier.setNotes(supplier.getNotes() + "\nSuspended: " + reason);
        supplier.setUpdatedAt(LocalDateTime.now());
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier suspended successfully with ID: {}", updatedSupplier.getId());
        
        return convertToDTO(updatedSupplier);
    }
    
    @Override
    public SupplierDTO blacklistSupplier(Long id, String reason) {
        log.info("Blacklisting supplier with ID: {} for reason: {}", id, reason);
        
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        
        supplier.setStatus(Supplier.Status.BLACKLISTED);
        supplier.setNotes(supplier.getNotes() + "\nBlacklisted: " + reason);
        supplier.setUpdatedAt(LocalDateTime.now());
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier blacklisted successfully with ID: {}", updatedSupplier.getId());
        
        return convertToDTO(updatedSupplier);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isSupplierCodeUnique(String supplierCode, Long excludeId) {
        Optional<Supplier> existingSupplier = supplierRepository.findBySupplierCode(supplierCode);
        return existingSupplier.isEmpty() || existingSupplier.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailUnique(String email, Long excludeId) {
        Optional<Supplier> existingSupplier = supplierRepository.findByEmail(email);
        return existingSupplier.isEmpty() || existingSupplier.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isGstNumberUnique(String gstNumber, Long excludeId) {
        Optional<Supplier> existingSupplier = supplierRepository.findByGstNumber(gstNumber);
        return existingSupplier.isEmpty() || existingSupplier.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPanNumberUnique(String panNumber, Long excludeId) {
        Optional<Supplier> existingSupplier = supplierRepository.findByPanNumber(panNumber);
        return existingSupplier.isEmpty() || existingSupplier.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierDashboard(Long pumpId) {
        log.info("Getting supplier dashboard for pump: {}", pumpId);
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Total suppliers
        long totalSuppliers = supplierRepository.countByStatusAndPumpId(Supplier.Status.ACTIVE, pumpId);
        dashboard.put("totalSuppliers", totalSuppliers);
        
        // Active suppliers
        long activeSuppliers = supplierRepository.countByStatusAndPumpId(Supplier.Status.ACTIVE, pumpId);
        dashboard.put("activeSuppliers", activeSuppliers);
        
        // Inactive suppliers
        long inactiveSuppliers = supplierRepository.countByStatusAndPumpId(Supplier.Status.INACTIVE, pumpId);
        dashboard.put("inactiveSuppliers", inactiveSuppliers);
        
        // Suspended suppliers
        long suspendedSuppliers = supplierRepository.countByStatusAndPumpId(Supplier.Status.SUSPENDED, pumpId);
        dashboard.put("suspendedSuppliers", suspendedSuppliers);
        
        // Suppliers with credit limit
        List<SupplierDTO> suppliersWithCredit = getSuppliersWithCreditLimit(pumpId);
        dashboard.put("suppliersWithCreditLimit", suppliersWithCredit.size());
        
        return dashboard;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierSummary(Long pumpId) {
        log.info("Getting supplier summary for pump: {}", pumpId);
        
        Map<String, Object> summary = new HashMap<>();
        
        List<Supplier> allSuppliers = supplierRepository.findByPumpIdOrderBySupplierNameAsc(pumpId);
        
        // Status distribution
        Map<String, Long> statusDistribution = allSuppliers.stream()
                .collect(Collectors.groupingBy(
                    supplier -> supplier.getStatus().toString(),
                    Collectors.counting()
                ));
        summary.put("statusDistribution", statusDistribution);
        
        // Top suppliers by credit limit
        List<SupplierDTO> topSuppliersByCredit = getSuppliersWithCreditLimit(pumpId)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
        summary.put("topSuppliersByCreditLimit", topSuppliersByCredit);
        
        return summary;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getTopSuppliersByPurchaseAmount(Long pumpId, int limit) {
        log.info("Getting top suppliers by purchase amount for pump: {} with limit: {}", pumpId, limit);
        
        // This would require a join with purchase bills table
        // For now, return suppliers with credit limit as a proxy
        return getSuppliersWithCreditLimit(pumpId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public String generateSupplierCode(Long pumpId) {
        log.info("Generating supplier code for pump: {}", pumpId);
        
        // Generate code in format: SUP-YYYY-NNNN (where NNNN is sequential number)
        String year = String.valueOf(LocalDateTime.now().getYear());
        
        // Find the last supplier code for this year
        List<Supplier> suppliers = supplierRepository.findByPumpIdOrderBySupplierNameAsc(pumpId);
        int maxSequence = 0;
        
        for (Supplier supplier : suppliers) {
            String code = supplier.getSupplierCode();
            if (code != null && code.startsWith("SUP-" + year + "-")) {
                try {
                    int sequence = Integer.parseInt(code.substring(8));
                    maxSequence = Math.max(maxSequence, sequence);
                } catch (NumberFormatException e) {
                    // Ignore invalid codes
                }
            }
        }
        
        return String.format("SUP-%s-%04d", year, maxSequence + 1);
    }
    
    // Helper methods
    private Supplier convertToEntity(SupplierDTO dto) {
        return Supplier.builder()
                .id(dto.getId())
                .supplierCode(dto.getSupplierCode())
                .supplierName(dto.getSupplierName())
                .contactPerson(dto.getContactPerson())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .mobile(dto.getMobile())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .country(dto.getCountry())
                .gstNumber(dto.getGstNumber())
                .panNumber(dto.getPanNumber())
                .bankName(dto.getBankName())
                .bankAccountNumber(dto.getBankAccountNumber())
                .bankIfsc(dto.getBankIfsc())
                .creditLimit(dto.getCreditLimit())
                .paymentTerms(dto.getPaymentTerms())
                .status(dto.getStatus() != null ? dto.getStatus() : Supplier.Status.ACTIVE)
                .notes(dto.getNotes())
                .pumpId(dto.getPumpId())
                .build();
    }
    
    private SupplierDTO convertToDTO(Supplier entity) {
        SupplierDTO dto = SupplierDTO.builder()
                .id(entity.getId())
                .supplierCode(entity.getSupplierCode())
                .supplierName(entity.getSupplierName())
                .contactPerson(entity.getContactPerson())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .mobile(entity.getMobile())
                .address(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .pincode(entity.getPincode())
                .country(entity.getCountry())
                .gstNumber(entity.getGstNumber())
                .panNumber(entity.getPanNumber())
                .bankName(entity.getBankName())
                .bankAccountNumber(entity.getBankAccountNumber())
                .bankIfsc(entity.getBankIfsc())
                .creditLimit(entity.getCreditLimit())
                .paymentTerms(entity.getPaymentTerms())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .pumpId(entity.getPumpId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        
        // Set display fields
        dto.setStatusDisplay(entity.getStatus().toString());
        dto.setFullAddress(entity.getFullAddress());
        
        return dto;
    }
    
    private void updateSupplierFields(Supplier existing, SupplierDTO dto) {
        existing.setSupplierCode(dto.getSupplierCode());
        existing.setSupplierName(dto.getSupplierName());
        existing.setContactPerson(dto.getContactPerson());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setMobile(dto.getMobile());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setPincode(dto.getPincode());
        existing.setCountry(dto.getCountry());
        existing.setGstNumber(dto.getGstNumber());
        existing.setPanNumber(dto.getPanNumber());
        existing.setBankName(dto.getBankName());
        existing.setBankAccountNumber(dto.getBankAccountNumber());
        existing.setBankIfsc(dto.getBankIfsc());
        existing.setCreditLimit(dto.getCreditLimit());
        existing.setPaymentTerms(dto.getPaymentTerms());
        existing.setStatus(dto.getStatus());
        existing.setNotes(dto.getNotes());
    }
}
