package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.CashCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashCollectionRepository extends JpaRepository<CashCollection, Long> {

    List<CashCollection> findByPumpIdOrderByCollectionDateDesc(Long pumpId);

    List<CashCollection> findByShiftIdOrderByCollectionDateDesc(Long shiftId);

    List<CashCollection> findByCashierIdOrderByCollectionDateDesc(Long cashierId);

    List<CashCollection> findByStatusOrderByCollectionDateDesc(CashCollection.Status status);

    @Query("SELECT cc FROM CashCollection cc WHERE cc.pumpId = :pumpId AND cc.collectionDate BETWEEN :startDate AND :endDate ORDER BY cc.collectionDate DESC")
    List<CashCollection> findByPumpIdAndDateRange(@Param("pumpId") Long pumpId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT cc FROM CashCollection cc WHERE cc.shiftId = :shiftId AND cc.status = :status")
    Optional<CashCollection> findByShiftIdAndStatus(@Param("shiftId") Long shiftId, 
                                                    @Param("status") CashCollection.Status status);

    @Query("SELECT cc FROM CashCollection cc WHERE cc.cashierId = :cashierId AND cc.collectionDate >= :fromDate ORDER BY cc.collectionDate DESC")
    List<CashCollection> findRecentCollectionsByCashier(@Param("cashierId") Long cashierId, 
                                                        @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT cc FROM CashCollection cc WHERE cc.pumpId = :pumpId AND cc.collectionDate >= :fromDate AND cc.status IN :statuses ORDER BY cc.collectionDate DESC")
    List<CashCollection> findByPumpIdAndDateFromAndStatusIn(@Param("pumpId") Long pumpId, 
                                                            @Param("fromDate") LocalDateTime fromDate, 
                                                            @Param("statuses") List<CashCollection.Status> statuses);

    @Query("SELECT SUM(cc.totalSales) FROM CashCollection cc WHERE cc.pumpId = :pumpId AND cc.collectionDate BETWEEN :startDate AND :endDate")
    Double sumTotalSalesByPumpAndDateRange(@Param("pumpId") Long pumpId, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(cc.cashCollected) FROM CashCollection cc WHERE cc.shiftId = :shiftId")
    Double sumCashCollectedByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT cc FROM CashCollection cc WHERE cc.collectedBy = :userId ORDER BY cc.collectionDate DESC")
    List<CashCollection> findByCollectedBy(@Param("userId") Long userId);

    @Query("SELECT cc FROM CashCollection cc WHERE cc.verifiedBy = :userId ORDER BY cc.verifiedAt DESC")
    List<CashCollection> findByVerifiedBy(@Param("userId") Long userId);
}
