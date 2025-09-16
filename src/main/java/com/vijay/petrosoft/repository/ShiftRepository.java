package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByPumpId(Long pumpId);
    List<Shift> findByOperatorId(Long operatorId);
    List<Shift> findByOpenedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Shift> findByStatus(Shift.Status status);
    
    // Additional methods needed by services
    List<Shift> findByPumpIdAndStatus(Long pumpId, Shift.Status status);
    
    Optional<Shift> findTopByPumpIdOrderByOpenedAtDesc(Long pumpId);
    
    Optional<Shift> findTopByOperatorIdOrderByOpenedAtDesc(Long operatorId);
    
    List<Shift> findByPumpIdAndOpenedAtBetween(Long pumpId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Shift> findByOperatorIdAndOpenedAtBetween(Long operatorId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Shift> findByStatusAndOpenedAtBetween(Shift.Status status, LocalDateTime startDate, LocalDateTime endDate);
}