package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.domain.Supplier;
import com.vijay.petrosoft.dto.SupplierDTO;
import com.vijay.petrosoft.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {
    
    private final SupplierService supplierService;
    
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        log.info("Creating supplier: {}", supplierDTO.getSupplierName());
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        log.info("Getting supplier by ID: {}", id);
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }
    
    @GetMapping("/code/{supplierCode}")
    public ResponseEntity<SupplierDTO> getSupplierByCode(@PathVariable String supplierCode) {
        log.info("Getting supplier by code: {}", supplierCode);
        SupplierDTO supplier = supplierService.getSupplierByCode(supplierCode);
        return ResponseEntity.ok(supplier);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDTO supplierDTO) {
        log.info("Updating supplier with ID: {}", id);
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        log.info("Deleting supplier with ID: {}", id);
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
    
    // Query Endpoints
    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByPump(@PathVariable Long pumpId) {
        log.info("Getting suppliers by pump ID: {}", pumpId);
        List<SupplierDTO> suppliers = supplierService.getSuppliersByPump(pumpId);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/pump/{pumpId}/paginated")
    public ResponseEntity<Page<SupplierDTO>> getSuppliersByPumpPaginated(@PathVariable Long pumpId, Pageable pageable) {
        log.info("Getting suppliers by pump ID: {} with pagination", pumpId);
        Page<SupplierDTO> suppliers = supplierService.getSuppliersByPump(pumpId, pageable);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByStatus(@PathVariable Supplier.Status status) {
        log.info("Getting suppliers by status: {}", status);
        List<SupplierDTO> suppliers = supplierService.getSuppliersByStatus(status);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByCity(@PathVariable String city) {
        log.info("Getting suppliers by city: {}", city);
        List<SupplierDTO> suppliers = supplierService.getSuppliersByCity(city);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/state/{state}")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByState(@PathVariable String state) {
        log.info("Getting suppliers by state: {}", state);
        List<SupplierDTO> suppliers = supplierService.getSuppliersByState(state);
        return ResponseEntity.ok(suppliers);
    }
    
    // Search Endpoints
    @GetMapping("/pump/{pumpId}/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(@PathVariable Long pumpId, @RequestParam String searchTerm) {
        log.info("Searching suppliers by term: {} for pump: {}", searchTerm, pumpId);
        List<SupplierDTO> suppliers = supplierService.searchSuppliers(pumpId, searchTerm);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/pump/{pumpId}/with-credit-limit")
    public ResponseEntity<List<SupplierDTO>> getSuppliersWithCreditLimit(@PathVariable Long pumpId) {
        log.info("Getting suppliers with credit limit for pump: {}", pumpId);
        List<SupplierDTO> suppliers = supplierService.getSuppliersWithCreditLimit(pumpId);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/pump/{pumpId}/payment-terms")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByPaymentTerms(@PathVariable Long pumpId, @RequestParam String paymentTerms) {
        log.info("Getting suppliers by payment terms: {} for pump: {}", paymentTerms, pumpId);
        List<SupplierDTO> suppliers = supplierService.getSuppliersByPaymentTerms(pumpId, paymentTerms);
        return ResponseEntity.ok(suppliers);
    }
    
    // Status Management Endpoints
    @PostMapping("/{id}/activate")
    public ResponseEntity<SupplierDTO> activateSupplier(@PathVariable Long id) {
        log.info("Activating supplier with ID: {}", id);
        SupplierDTO updatedSupplier = supplierService.activateSupplier(id);
        return ResponseEntity.ok(updatedSupplier);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<SupplierDTO> deactivateSupplier(@PathVariable Long id) {
        log.info("Deactivating supplier with ID: {}", id);
        SupplierDTO updatedSupplier = supplierService.deactivateSupplier(id);
        return ResponseEntity.ok(updatedSupplier);
    }
    
    @PostMapping("/{id}/suspend")
    public ResponseEntity<SupplierDTO> suspendSupplier(@PathVariable Long id, @RequestParam String reason) {
        log.info("Suspending supplier with ID: {} for reason: {}", id, reason);
        SupplierDTO updatedSupplier = supplierService.suspendSupplier(id, reason);
        return ResponseEntity.ok(updatedSupplier);
    }
    
    @PostMapping("/{id}/blacklist")
    public ResponseEntity<SupplierDTO> blacklistSupplier(@PathVariable Long id, @RequestParam String reason) {
        log.info("Blacklisting supplier with ID: {} for reason: {}", id, reason);
        SupplierDTO updatedSupplier = supplierService.blacklistSupplier(id, reason);
        return ResponseEntity.ok(updatedSupplier);
    }
    
    // Reporting Endpoints
    @GetMapping("/pump/{pumpId}/dashboard")
    public ResponseEntity<Map<String, Object>> getSupplierDashboard(@PathVariable Long pumpId) {
        log.info("Getting supplier dashboard for pump: {}", pumpId);
        Map<String, Object> dashboard = supplierService.getSupplierDashboard(pumpId);
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/pump/{pumpId}/summary")
    public ResponseEntity<Map<String, Object>> getSupplierSummary(@PathVariable Long pumpId) {
        log.info("Getting supplier summary for pump: {}", pumpId);
        Map<String, Object> summary = supplierService.getSupplierSummary(pumpId);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/pump/{pumpId}/top-suppliers")
    public ResponseEntity<List<SupplierDTO>> getTopSuppliersByPurchaseAmount(@PathVariable Long pumpId, @RequestParam(defaultValue = "10") int limit) {
        log.info("Getting top suppliers by purchase amount for pump: {} with limit: {}", pumpId, limit);
        List<SupplierDTO> suppliers = supplierService.getTopSuppliersByPurchaseAmount(pumpId, limit);
        return ResponseEntity.ok(suppliers);
    }
    
    // Validation Endpoints
    @GetMapping("/validate/supplier-code")
    public ResponseEntity<Map<String, Boolean>> validateSupplierCode(@RequestParam String supplierCode, @RequestParam(required = false) Long excludeId) {
        log.info("Validating supplier code: {}", supplierCode);
        boolean isUnique = supplierService.isSupplierCodeUnique(supplierCode, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }
    
    @GetMapping("/validate/email")
    public ResponseEntity<Map<String, Boolean>> validateEmail(@RequestParam String email, @RequestParam(required = false) Long excludeId) {
        log.info("Validating email: {}", email);
        boolean isUnique = supplierService.isEmailUnique(email, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }
    
    @GetMapping("/validate/gst-number")
    public ResponseEntity<Map<String, Boolean>> validateGstNumber(@RequestParam String gstNumber, @RequestParam(required = false) Long excludeId) {
        log.info("Validating GST number: {}", gstNumber);
        boolean isUnique = supplierService.isGstNumberUnique(gstNumber, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }
    
    @GetMapping("/validate/pan-number")
    public ResponseEntity<Map<String, Boolean>> validatePanNumber(@RequestParam String panNumber, @RequestParam(required = false) Long excludeId) {
        log.info("Validating PAN number: {}", panNumber);
        boolean isUnique = supplierService.isPanNumberUnique(panNumber, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }
    
    // Utility Endpoints
    @GetMapping("/generate-supplier-code")
    public ResponseEntity<Map<String, String>> generateSupplierCode(@RequestParam Long pumpId) {
        log.info("Generating supplier code for pump: {}", pumpId);
        String supplierCode = supplierService.generateSupplierCode(pumpId);
        return ResponseEntity.ok(Map.of("supplierCode", supplierCode));
    }
}
