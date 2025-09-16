package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Purchase;
import com.vijay.petrosoft.dto.PurchaseDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PurchaseService {
    PurchaseDTO createPurchase(PurchaseDTO purchaseDTO);
    PurchaseDTO updatePurchase(Long id, PurchaseDTO purchaseDTO);
    Optional<PurchaseDTO> getPurchaseById(Long id);
    List<PurchaseDTO> getAllPurchases();
    void deletePurchase(Long id);
    List<PurchaseDTO> getPurchasesByPumpId(Long pumpId);
    List<PurchaseDTO> getPurchasesByVendorId(Long vendorId);
    List<PurchaseDTO> getPurchasesByDateRange(LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalPurchasesByDate(LocalDate date);
    BigDecimal getTotalPurchasesByVendor(Long vendorId);
    boolean isInvoiceNumberExists(String invoiceNumber);
}
