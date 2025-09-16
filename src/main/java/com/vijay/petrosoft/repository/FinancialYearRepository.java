package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialYearRepository extends JpaRepository<FinancialYear, Long> {
    
    List<FinancialYear> findByPumpId(Long pumpId);
    
    List<FinancialYear> findByActiveTrue();
    
    Optional<FinancialYear> findByName(String name);
    
    @Query("SELECT fy FROM FinancialYear fy WHERE fy.pumpId = :pumpId AND fy.active = true")
    Optional<FinancialYear> findActiveFinancialYearByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT fy FROM FinancialYear fy WHERE fy.pumpId IS NULL AND fy.active = true")
    Optional<FinancialYear> findActiveGlobalFinancialYear();
    
    @Query("SELECT fy FROM FinancialYear fy WHERE :date BETWEEN fy.startDate AND fy.endDate")
    List<FinancialYear> findFinancialYearsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT fy FROM FinancialYear fy WHERE fy.pumpId = :pumpId AND :date BETWEEN fy.startDate AND fy.endDate")
    Optional<FinancialYear> findFinancialYearByPumpIdAndDate(@Param("pumpId") Long pumpId, @Param("date") LocalDate date);
    
    @Query("SELECT fy FROM FinancialYear fy WHERE fy.startDate <= :date AND fy.endDate >= :date AND fy.active = true")
    List<FinancialYear> findActiveFinancialYearsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT fy FROM FinancialYear fy WHERE fy.startDate > :date ORDER BY fy.startDate ASC")
    List<FinancialYear> findFutureFinancialYears(@Param("date") LocalDate date);
    
    @Query("SELECT fy FROM FinancialYear fy WHERE fy.endDate < :date ORDER BY fy.endDate DESC")
    List<FinancialYear> findPastFinancialYears(@Param("date") LocalDate date);
    
    @Query("SELECT fy FROM FinancialYear fy WHERE fy.pumpId IS NULL ORDER BY fy.startDate DESC")
    List<FinancialYear> findGlobalFinancialYears();
    
    boolean existsByNameAndPumpId(String name, Long pumpId);
}
