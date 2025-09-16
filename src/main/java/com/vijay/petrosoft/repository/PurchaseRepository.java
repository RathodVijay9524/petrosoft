package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
    List<Purchase> findByPumpId(Long pumpId);
    List<Purchase> findByVendorId(Long vendorId);
}