package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Customer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByOutstandingGreaterThan(BigDecimal amount);
    boolean existsByCode(String code);
    Optional<Customer> findByCode(String code);
}
