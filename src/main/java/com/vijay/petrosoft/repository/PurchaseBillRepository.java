package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.PurchaseBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseBillRepository extends JpaRepository<PurchaseBill, Long> {
    
    // Find by bill number
    Optional<PurchaseBill> findByBillNumber(String billNumber);
    
    // Find by pump
    List<PurchaseBill> findByPumpIdOrderByBillDateDesc(Long pumpId);
    Page<PurchaseBill> findByPumpIdOrderByBillDateDesc(Long pumpId, Pageable pageable);
    
    // Find by supplier
    List<PurchaseBill> findBySupplierIdOrderByBillDateDesc(Long supplierId);
    List<PurchaseBill> findBySupplierIdAndPumpIdOrderByBillDateDesc(Long supplierId, Long pumpId);
    
    // Find by status
    List<PurchaseBill> findByStatusOrderByBillDateDesc(PurchaseBill.Status status);
    
    // Find by payment status
    List<PurchaseBill> findByPaymentStatusOrderByBillDateDesc(PurchaseBill.PaymentStatus paymentStatus);
    List<PurchaseBill> findByPaymentStatusAndPumpIdOrderByBillDateDesc(PurchaseBill.PaymentStatus paymentStatus, Long pumpId);
    
    // Find by bill type
    List<PurchaseBill> findByBillTypeOrderByBillDateDesc(PurchaseBill.BillType billType);
    List<PurchaseBill> findByBillTypeAndPumpIdOrderByBillDateDesc(PurchaseBill.BillType billType, Long pumpId);
    
    // Find by date range
    List<PurchaseBill> findByBillDateBetweenOrderByBillDateDesc(LocalDate startDate, LocalDate endDate);
    List<PurchaseBill> findByBillDateBetweenAndPumpIdOrderByBillDateDesc(LocalDate startDate, LocalDate endDate, Long pumpId);
    
    // Find by due date (overdue bills)
    List<PurchaseBill> findByDueDateBeforeAndPaymentStatusNotOrderByDueDateAsc(LocalDate date, PurchaseBill.PaymentStatus paymentStatus);
    List<PurchaseBill> findByDueDateBeforeAndPaymentStatusNotAndPumpIdOrderByDueDateAsc(LocalDate date, PurchaseBill.PaymentStatus paymentStatus, Long pumpId);
    
    // Find approved bills
    List<PurchaseBill> findByStatusAndPumpIdOrderByBillDateDesc(PurchaseBill.Status status, Long pumpId);
    
    // Find bills by reference number
    List<PurchaseBill> findByReferenceNumberOrderByBillDateDesc(String referenceNumber);
    
    // Find bills by vehicle number
    List<PurchaseBill> findByVehicleNumberOrderByBillDateDesc(String vehicleNumber);
    
    // Custom queries for reporting
    @Query("SELECT pb FROM PurchaseBill pb WHERE pb.pumpId = :pumpId AND pb.status = :status AND pb.billDate BETWEEN :startDate AND :endDate ORDER BY pb.billDate DESC")
    List<PurchaseBill> findBillsByPumpAndStatusAndDateRange(@Param("pumpId") Long pumpId, 
                                                           @Param("status") PurchaseBill.Status status, 
                                                           @Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(pb.totalAmount) FROM PurchaseBill pb WHERE pb.pumpId = :pumpId AND pb.billDate BETWEEN :startDate AND :endDate")
    Double sumTotalAmountByPumpAndDateRange(@Param("pumpId") Long pumpId, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(pb) FROM PurchaseBill pb WHERE pb.pumpId = :pumpId AND pb.billDate BETWEEN :startDate AND :endDate")
    Long countBillsByPumpAndDateRange(@Param("pumpId") Long pumpId, 
                                     @Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT pb FROM PurchaseBill pb WHERE pb.supplier.id = :supplierId AND pb.status IN :statuses ORDER BY pb.billDate DESC")
    List<PurchaseBill> findBillsBySupplierAndStatuses(@Param("supplierId") Long supplierId, 
                                                      @Param("statuses") List<PurchaseBill.Status> statuses);
    
    // Find bills pending approval
    @Query("SELECT pb FROM PurchaseBill pb WHERE pb.status = 'SUBMITTED' AND pb.pumpId = :pumpId ORDER BY pb.billDate ASC")
    List<PurchaseBill> findPendingApprovalBills(@Param("pumpId") Long pumpId);
    
    // Find overdue bills
    @Query("SELECT pb FROM PurchaseBill pb WHERE pb.dueDate < :currentDate AND pb.paymentStatus IN ('PENDING', 'PARTIAL') AND pb.pumpId = :pumpId ORDER BY pb.dueDate ASC")
    List<PurchaseBill> findOverdueBills(@Param("currentDate") LocalDate currentDate, @Param("pumpId") Long pumpId);
    
    // Count methods for dashboard
    @Query("SELECT COUNT(pb) FROM PurchaseBill pb WHERE pb.status = :status AND pb.pumpId = :pumpId")
    Long countByStatusAndPumpId(@Param("status") PurchaseBill.Status status, @Param("pumpId") Long pumpId);
}
