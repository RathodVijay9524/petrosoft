package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByPumpId(Long pumpId);
    
    List<Subscription> findByActiveTrue();
    
    List<Subscription> findByActiveFalse();
    
    @Query("SELECT s FROM Subscription s WHERE s.pumpId = :pumpId AND s.active = true")
    Optional<Subscription> findActiveSubscriptionByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT s FROM Subscription s WHERE s.nextPaymentDate <= :date AND s.active = true")
    List<Subscription> findSubscriptionsDueForPayment(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Subscription s WHERE s.endsAt <= :date AND s.active = true")
    List<Subscription> findExpiredSubscriptions(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Subscription s WHERE s.endsAt BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.active = true")
    Long countActiveSubscriptions();
    
    @Query("SELECT s FROM Subscription s WHERE s.paymentStatus = :status")
    List<Subscription> findByPaymentStatus(@Param("status") Subscription.PaymentStatus status);
}
