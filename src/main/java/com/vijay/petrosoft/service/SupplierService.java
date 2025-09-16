package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Supplier;
import com.vijay.petrosoft.dto.SupplierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SupplierService {
    
    // Supplier CRUD Operations
    SupplierDTO createSupplier(SupplierDTO supplierDTO);
    SupplierDTO getSupplierById(Long id);
    SupplierDTO getSupplierByCode(String supplierCode);
    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);
    void deleteSupplier(Long id);
    
    // Supplier Queries
    List<SupplierDTO> getSuppliersByPump(Long pumpId);
    Page<SupplierDTO> getSuppliersByPump(Long pumpId, Pageable pageable);
    List<SupplierDTO> getSuppliersByStatus(Supplier.Status status);
    List<SupplierDTO> getSuppliersByCity(String city);
    List<SupplierDTO> getSuppliersByState(String state);
    
    // Search Operations
    List<SupplierDTO> searchSuppliers(Long pumpId, String searchTerm);
    List<SupplierDTO> getSuppliersWithCreditLimit(Long pumpId);
    List<SupplierDTO> getSuppliersByPaymentTerms(Long pumpId, String paymentTerms);
    
    // Status Management
    SupplierDTO activateSupplier(Long id);
    SupplierDTO deactivateSupplier(Long id);
    SupplierDTO suspendSupplier(Long id, String reason);
    SupplierDTO blacklistSupplier(Long id, String reason);
    
    // Validation
    boolean isSupplierCodeUnique(String supplierCode, Long excludeId);
    boolean isEmailUnique(String email, Long excludeId);
    boolean isGstNumberUnique(String gstNumber, Long excludeId);
    boolean isPanNumberUnique(String panNumber, Long excludeId);
    
    // Reporting
    Map<String, Object> getSupplierDashboard(Long pumpId);
    Map<String, Object> getSupplierSummary(Long pumpId);
    List<SupplierDTO> getTopSuppliersByPurchaseAmount(Long pumpId, int limit);
    
    // Code Generation
    String generateSupplierCode(Long pumpId);
}
