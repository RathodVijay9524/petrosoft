package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Tank;
import java.util.List;

public interface TankRepository extends JpaRepository<Tank, Long> {
    List<Tank> findByPumpId(Long pumpId);
}
