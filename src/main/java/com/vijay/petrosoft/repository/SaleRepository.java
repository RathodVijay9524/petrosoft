package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.SaleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<SaleTransaction, Long> {

    List<SaleTransaction> findByPumpIdOrderByTransactedAtDesc(Long pumpId);
    List<SaleTransaction> findByShiftIdOrderByTransactedAtDesc(Long shiftId);
    List<SaleTransaction> findByTransactedAtBetweenOrderByTransactedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    List<SaleTransaction> findByCustomerIdOrderByTransactedAtDesc(Long customerId);

    @Query("SELECT s FROM SaleTransaction s WHERE s.pumpId = :pumpId AND s.transactedAt BETWEEN :startDate AND :endDate ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByPumpIdAndTransactedAtBetween(@Param("pumpId") Long pumpId, 
                                                             @Param("startDate") LocalDateTime startDate, 
                                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM SaleTransaction s WHERE s.shift.id = :shiftId AND s.paymentMethod = :paymentMethod ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByShiftIdAndPaymentMethod(@Param("shiftId") Long shiftId, 
                                                        @Param("paymentMethod") SaleTransaction.PaymentMethod paymentMethod);

    @Query("SELECT s FROM SaleTransaction s WHERE s.pumpId = :pumpId AND s.paymentMethod = :paymentMethod ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByPumpIdAndPaymentMethod(@Param("pumpId") Long pumpId, 
                                                       @Param("paymentMethod") SaleTransaction.PaymentMethod paymentMethod);

    @Query("SELECT s FROM SaleTransaction s WHERE s.shift.id = :shiftId AND s.status = :status ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByShiftIdAndStatus(@Param("shiftId") Long shiftId, 
                                                 @Param("status") SaleTransaction.Status status);

    @Query("SELECT s FROM SaleTransaction s WHERE s.operatorId = :operatorId AND s.transactedAt BETWEEN :startDate AND :endDate ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByOperatorIdAndTransactedAtBetween(@Param("operatorId") Long operatorId, 
                                                                 @Param("startDate") LocalDateTime startDate, 
                                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM SaleTransaction s WHERE s.cashierId = :cashierId AND s.transactedAt BETWEEN :startDate AND :endDate ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByCashierIdAndTransactedAtBetween(@Param("cashierId") Long cashierId, 
                                                                @Param("startDate") LocalDateTime startDate, 
                                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM SaleTransaction s WHERE s.saleNumber = :saleNumber")
    Optional<SaleTransaction> findBySaleNumber(@Param("saleNumber") String saleNumber);

    @Query("SELECT s FROM SaleTransaction s WHERE s.transactionReference = :transactionReference")
    Optional<SaleTransaction> findByTransactionReference(@Param("transactionReference") String transactionReference);

    @Query("SELECT s FROM SaleTransaction s WHERE s.vehicleNumber = :vehicleNumber ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByVehicleNumber(@Param("vehicleNumber") String vehicleNumber);

    @Query("SELECT SUM(s.totalAmount) FROM SaleTransaction s WHERE s.shift.id = :shiftId")
    Double sumTotalAmountByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(s.totalAmount) FROM SaleTransaction s WHERE s.shift.id = :shiftId AND s.paymentMethod = :paymentMethod")
    Double sumTotalAmountByShiftIdAndPaymentMethod(@Param("shiftId") Long shiftId, 
                                                   @Param("paymentMethod") SaleTransaction.PaymentMethod paymentMethod);

    @Query("SELECT COUNT(s) FROM SaleTransaction s WHERE s.shift.id = :shiftId")
    Long countByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT COUNT(s) FROM SaleTransaction s WHERE s.shift.id = :shiftId AND s.paymentMethod = :paymentMethod")
    Long countByShiftIdAndPaymentMethod(@Param("shiftId") Long shiftId, 
                                        @Param("paymentMethod") SaleTransaction.PaymentMethod paymentMethod);

    @Query("SELECT SUM(s.quantity) FROM SaleTransaction s WHERE s.shift.id = :shiftId")
    Double sumQuantityByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT s FROM SaleTransaction s WHERE s.status = :status ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findByStatus(@Param("status") SaleTransaction.Status status);

    @Query("SELECT s FROM SaleTransaction s WHERE s.saleType = :saleType ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findBySaleType(@Param("saleType") SaleTransaction.SaleType saleType);

    @Query("SELECT s FROM SaleTransaction s WHERE s.pumpId = :pumpId AND s.transactedAt >= :fromDate ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findRecentSalesByPump(@Param("pumpId") Long pumpId, 
                                                @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT s FROM SaleTransaction s WHERE s.customer.id = :customerId AND s.paymentMethod = 'CREDIT' AND s.status = 'COMPLETED' ORDER BY s.transactedAt DESC")
    List<SaleTransaction> findCreditSalesByCustomer(@Param("customerId") Long customerId);
}
