package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.CashMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashMovementRepository extends JpaRepository<CashMovement, Long> {

    List<CashMovement> findByPumpIdOrderByMovementDateDesc(Long pumpId);

    List<CashMovement> findByShiftIdOrderByMovementDateDesc(Long shiftId);

    List<CashMovement> findByPumpIdAndMovementDateBetweenOrderByMovementDateDesc(
            Long pumpId, LocalDateTime startDate, LocalDateTime endDate);

    List<CashMovement> findByMovementTypeOrderByMovementDateDesc(CashMovement.MovementType movementType);

    List<CashMovement> findByStatusOrderByMovementDateDesc(CashMovement.Status status);

    @Query("SELECT cm FROM CashMovement cm WHERE cm.pumpId = :pumpId AND cm.movementType = :movementType ORDER BY cm.movementDate DESC")
    List<CashMovement> findByPumpIdAndMovementType(@Param("pumpId") Long pumpId, 
                                                   @Param("movementType") CashMovement.MovementType movementType);

    @Query("SELECT cm FROM CashMovement cm WHERE cm.shiftId = :shiftId AND cm.status = :status ORDER BY cm.movementDate DESC")
    List<CashMovement> findByShiftIdAndStatus(@Param("shiftId") Long shiftId, 
                                              @Param("status") CashMovement.Status status);

    @Query("SELECT cm FROM CashMovement cm WHERE cm.pumpId = :pumpId AND cm.movementDate >= :fromDate ORDER BY cm.movementDate DESC")
    List<CashMovement> findRecentMovementsByPump(@Param("pumpId") Long pumpId, 
                                                 @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT SUM(cm.amount) FROM CashMovement cm WHERE cm.shiftId = :shiftId AND cm.movementType = :movementType")
    Double sumAmountByShiftAndType(@Param("shiftId") Long shiftId, 
                                   @Param("movementType") CashMovement.MovementType movementType);

    @Query("SELECT cm FROM CashMovement cm WHERE cm.referenceNumber = :referenceNumber")
    Optional<CashMovement> findByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query("SELECT cm FROM CashMovement cm WHERE cm.pumpId = :pumpId AND cm.movementDate BETWEEN :startDate AND :endDate AND cm.status = :status ORDER BY cm.movementDate DESC")
    List<CashMovement> findByPumpAndDateRangeAndStatus(@Param("pumpId") Long pumpId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate, 
                                                       @Param("status") CashMovement.Status status);
}
