package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.SaleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {
    List<SaleTransaction> findByTransactedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<SaleTransaction> findByPumpId(Long pumpId);
    List<SaleTransaction> findByCustomerId(Long customerId);
}
