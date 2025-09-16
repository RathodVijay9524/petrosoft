package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.MasterSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MasterSetupRepository extends JpaRepository<MasterSetup, Long> {
    
    Optional<MasterSetup> findByPumpId(Long pumpId);
    
    List<MasterSetup> findByActiveTrue();
    
    List<MasterSetup> findBySubscriptionActiveTrue();
    
    @Query("SELECT ms FROM MasterSetup ms WHERE ms.subscriptionExpiry <= :date AND ms.subscriptionActive = true")
    List<MasterSetup> findExpiringSubscriptions(@Param("date") LocalDateTime date);
    
    @Query("SELECT ms FROM MasterSetup ms WHERE ms.subscriptionExpiry <= :date AND ms.subscriptionActive = true")
    List<MasterSetup> findExpiredSubscriptions(@Param("date") LocalDateTime date);
    
    @Query("SELECT ms FROM MasterSetup ms WHERE ms.companyName LIKE %:companyName%")
    List<MasterSetup> findByCompanyNameContaining(@Param("companyName") String companyName);
    
    @Query("SELECT ms FROM MasterSetup ms WHERE ms.gstNumber = :gstNumber")
    Optional<MasterSetup> findByGstNumber(@Param("gstNumber") String gstNumber);
    
    @Query("SELECT ms FROM MasterSetup ms WHERE ms.currentFinancialYear = :financialYear")
    List<MasterSetup> findByCurrentFinancialYear(@Param("financialYear") String financialYear);
    
    @Query("SELECT ms FROM MasterSetup ms WHERE ms.pumpId = :pumpId AND ms.active = true")
    Optional<MasterSetup> findActiveMasterSetupByPumpId(@Param("pumpId") Long pumpId);
    
    boolean existsByPumpId(Long pumpId);
    
    boolean existsByGstNumber(String gstNumber);
    
    @Query("SELECT COUNT(ms) FROM MasterSetup ms WHERE ms.subscriptionActive = true")
    long countBySubscriptionActiveTrue();
    
    @Query("SELECT COUNT(ms) FROM MasterSetup ms WHERE ms.subscriptionExpiry <= :date")
    long countExpiringSubscriptions(@Param("date") LocalDateTime date);
}
