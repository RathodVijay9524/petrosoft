package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Pump;
import java.util.List;
import java.util.Optional;

public interface PumpRepository extends JpaRepository<Pump, Long> {
    List<Pump> findByCity(String city);
    List<Pump> findByState(String state);
    boolean existsByGstNumber(String gstNumber);
    Optional<Pump> findByGstNumber(String gstNumber);
}
