package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Nozzle;
import java.util.List;

public interface NozzleRepository extends JpaRepository<Nozzle, Long> {
    List<Nozzle> findByPumpId(Long pumpId);
}
