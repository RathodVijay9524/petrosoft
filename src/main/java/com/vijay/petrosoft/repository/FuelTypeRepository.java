package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.FuelType;
import java.util.Optional;

public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {
    boolean existsByName(String name);
    Optional<FuelType> findByName(String name);
}
