package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Purchase;
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {}
