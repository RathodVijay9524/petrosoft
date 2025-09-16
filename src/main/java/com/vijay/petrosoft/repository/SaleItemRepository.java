package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleTransactionIdOrderById(Long saleTransactionId);

    @Query("SELECT si FROM SaleItem si WHERE si.saleTransaction.pumpId = :pumpId ORDER BY si.createdAt DESC")
    List<SaleItem> findBySaleTransactionPumpId(@Param("pumpId") Long pumpId);

    @Query("SELECT si FROM SaleItem si WHERE si.saleTransaction.shift.id = :shiftId ORDER BY si.createdAt DESC")
    List<SaleItem> findBySaleTransactionShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT si FROM SaleItem si WHERE si.fuelType.id = :fuelTypeId ORDER BY si.createdAt DESC")
    List<SaleItem> findByFuelTypeId(@Param("fuelTypeId") Long fuelTypeId);

    @Query("SELECT si FROM SaleItem si WHERE si.nozzle.id = :nozzleId ORDER BY si.createdAt DESC")
    List<SaleItem> findByNozzleId(@Param("nozzleId") Long nozzleId);

    @Query("SELECT si FROM SaleItem si WHERE si.itemCode = :itemCode ORDER BY si.createdAt DESC")
    List<SaleItem> findByItemCode(@Param("itemCode") String itemCode);

    @Query("SELECT SUM(si.totalAmount) FROM SaleItem si WHERE si.saleTransaction.shift.id = :shiftId")
    Double sumTotalAmountByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.saleTransaction.shift.id = :shiftId")
    Double sumQuantityByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(si.totalAmount) FROM SaleItem si WHERE si.saleTransaction.shift.id = :shiftId AND si.fuelType.id = :fuelTypeId")
    Double sumTotalAmountByShiftIdAndFuelType(@Param("shiftId") Long shiftId, 
                                              @Param("fuelTypeId") Long fuelTypeId);

    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.saleTransaction.shift.id = :shiftId AND si.fuelType.id = :fuelTypeId")
    Double sumQuantityByShiftIdAndFuelType(@Param("shiftId") Long shiftId, 
                                           @Param("fuelTypeId") Long fuelTypeId);

    @Query("SELECT si FROM SaleItem si WHERE si.saleTransaction.id = :saleTransactionId AND si.fuelType.id = :fuelTypeId")
    List<SaleItem> findBySaleTransactionIdAndFuelTypeId(@Param("saleTransactionId") Long saleTransactionId, 
                                                        @Param("fuelTypeId") Long fuelTypeId);
}
