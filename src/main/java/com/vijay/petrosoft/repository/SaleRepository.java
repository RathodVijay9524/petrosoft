package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.SaleTransaction;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<SaleTransaction, Long> {
    List<SaleTransaction> findByPumpId(Long pumpId);
    List<SaleTransaction> findByShiftId(Long shiftId);
    List<SaleTransaction> findByTransactedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<SaleTransaction> findByCustomerId(Long customerId);
    List<SaleTransaction> findByCreditSaleTrue();
    List<SaleTransaction> findByCreditSaleFalse();
}
