package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Shift;
import java.time.LocalDateTime;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByPumpId(Long pumpId);
    List<Shift> findByOperatorId(Long operatorId);
    List<Shift> findByStatus(String status);
    List<Shift> findByPumpIdAndStatus(Long pumpId, String status);
    List<Shift> findByOpenedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
