package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.ShiftSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftSalesSummaryRepository extends JpaRepository<ShiftSalesSummary, Long> {

    List<ShiftSalesSummary> findByPumpIdOrderBySalesDateDesc(Long pumpId);

    List<ShiftSalesSummary> findByShiftIdOrderBySalesDateDesc(Long shiftId);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.shift.id = :shiftId AND sss.status = :status")
    Optional<ShiftSalesSummary> findByShiftIdAndStatus(@Param("shiftId") Long shiftId, 
                                                       @Param("status") ShiftSalesSummary.Status status);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.pumpId = :pumpId AND sss.salesDate BETWEEN :startDate AND :endDate ORDER BY sss.salesDate DESC")
    List<ShiftSalesSummary> findByPumpIdAndSalesDateBetween(@Param("pumpId") Long pumpId, 
                                                           @Param("startDate") LocalDateTime startDate, 
                                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.cashier.id = :cashierId AND sss.salesDate BETWEEN :startDate AND :endDate ORDER BY sss.salesDate DESC")
    List<ShiftSalesSummary> findByCashierIdAndSalesDateBetween(@Param("cashierId") Long cashierId, 
                                                              @Param("startDate") LocalDateTime startDate, 
                                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.status = :status ORDER BY sss.salesDate DESC")
    List<ShiftSalesSummary> findByStatus(@Param("status") ShiftSalesSummary.Status status);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.generatedBy = :userId ORDER BY sss.salesDate DESC")
    List<ShiftSalesSummary> findByGeneratedBy(@Param("userId") Long userId);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.approvedBy = :userId ORDER BY sss.approvedAt DESC")
    List<ShiftSalesSummary> findByApprovedBy(@Param("userId") Long userId);

    @Query("SELECT SUM(sss.totalSales) FROM ShiftSalesSummary sss WHERE sss.pumpId = :pumpId AND sss.salesDate BETWEEN :startDate AND :endDate")
    Double sumTotalSalesByPumpIdAndDateRange(@Param("pumpId") Long pumpId, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(sss.totalSales) FROM ShiftSalesSummary sss WHERE sss.shift.id = :shiftId")
    Double sumTotalSalesByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(sss.totalTransactions) FROM ShiftSalesSummary sss WHERE sss.pumpId = :pumpId AND sss.salesDate BETWEEN :startDate AND :endDate")
    Long sumTotalTransactionsByPumpIdAndDateRange(@Param("pumpId") Long pumpId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.pumpId = :pumpId AND sss.salesDate >= :fromDate ORDER BY sss.salesDate DESC")
    List<ShiftSalesSummary> findRecentSummariesByPump(@Param("pumpId") Long pumpId, 
                                                      @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT sss FROM ShiftSalesSummary sss WHERE sss.salesDate BETWEEN :startDate AND :endDate ORDER BY sss.salesDate DESC")
    List<ShiftSalesSummary> findBySalesDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
}
