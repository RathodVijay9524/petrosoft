package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.PurchaseBillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseBillItemRepository extends JpaRepository<PurchaseBillItem, Long> {
    
    // Find items by purchase bill
    List<PurchaseBillItem> findByPurchaseBillIdOrderById(Long purchaseBillId);
    
    // Find items by fuel type
    List<PurchaseBillItem> findByFuelTypeIdOrderByCreatedAtDesc(Long fuelTypeId);
    
    // Find fully received items
    @Query("SELECT pbi FROM PurchaseBillItem pbi WHERE pbi.receivedQuantity >= pbi.quantity")
    List<PurchaseBillItem> findFullyReceivedItems();
    
    // Find partially received items
    @Query("SELECT pbi FROM PurchaseBillItem pbi WHERE pbi.receivedQuantity > 0 AND pbi.receivedQuantity < pbi.quantity")
    List<PurchaseBillItem> findPartiallyReceivedItems();
    
    // Find pending items (not received)
    @Query("SELECT pbi FROM PurchaseBillItem pbi WHERE pbi.receivedQuantity IS NULL OR pbi.receivedQuantity = 0")
    List<PurchaseBillItem> findPendingItems();
    
    // Find items by purchase bill and fuel type
    List<PurchaseBillItem> findByPurchaseBillIdAndFuelTypeId(Long purchaseBillId, Long fuelTypeId);
    
    // Sum quantities by fuel type for a date range
    @Query("SELECT SUM(pbi.quantity) FROM PurchaseBillItem pbi " +
           "JOIN pbi.purchaseBill pb " +
           "WHERE pbi.fuelType.id = :fuelTypeId AND pb.pumpId = :pumpId " +
           "AND pb.billDate BETWEEN :startDate AND :endDate")
    Double sumQuantityByFuelTypeAndDateRange(@Param("fuelTypeId") Long fuelTypeId, 
                                            @Param("pumpId") Long pumpId, 
                                            @Param("startDate") java.time.LocalDate startDate, 
                                            @Param("endDate") java.time.LocalDate endDate);
    
    // Sum total amount by fuel type for a date range
    @Query("SELECT SUM(pbi.totalPrice) FROM PurchaseBillItem pbi " +
           "JOIN pbi.purchaseBill pb " +
           "WHERE pbi.fuelType.id = :fuelTypeId AND pb.pumpId = :pumpId " +
           "AND pb.billDate BETWEEN :startDate AND :endDate")
    Double sumTotalAmountByFuelTypeAndDateRange(@Param("fuelTypeId") Long fuelTypeId, 
                                               @Param("pumpId") Long pumpId, 
                                               @Param("startDate") java.time.LocalDate startDate, 
                                               @Param("endDate") java.time.LocalDate endDate);
    
    // Delete items by purchase bill ID
    void deleteByPurchaseBillId(Long purchaseBillId);
}
