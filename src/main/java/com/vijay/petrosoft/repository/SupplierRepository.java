package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    // Find by supplier code
    Optional<Supplier> findBySupplierCode(String supplierCode);
    
    // Find by pump
    List<Supplier> findByPumpIdOrderBySupplierNameAsc(Long pumpId);
    Page<Supplier> findByPumpIdOrderBySupplierNameAsc(Long pumpId, Pageable pageable);
    
    // Find by status
    List<Supplier> findByStatusOrderBySupplierNameAsc(Supplier.Status status);
    
    // Find by name (case insensitive)
    List<Supplier> findBySupplierNameContainingIgnoreCaseOrderBySupplierNameAsc(String supplierName);
    List<Supplier> findBySupplierNameContainingIgnoreCaseAndPumpIdOrderBySupplierNameAsc(String supplierName, Long pumpId);
    
    // Find by contact person
    List<Supplier> findByContactPersonContainingIgnoreCaseOrderBySupplierNameAsc(String contactPerson);
    
    // Find by email
    Optional<Supplier> findByEmail(String email);
    
    // Find by phone
    List<Supplier> findByPhone(String phone);
    List<Supplier> findByMobile(String mobile);
    
    // Find by GST number
    Optional<Supplier> findByGstNumber(String gstNumber);
    
    // Find by PAN number
    Optional<Supplier> findByPanNumber(String panNumber);
    
    // Find by city
    List<Supplier> findByCityOrderBySupplierNameAsc(String city);
    List<Supplier> findByCityAndPumpIdOrderBySupplierNameAsc(String city, Long pumpId);
    
    // Find by state
    List<Supplier> findByStateOrderBySupplierNameAsc(String state);
    List<Supplier> findByStateAndPumpIdOrderBySupplierNameAsc(String state, Long pumpId);
    
    // Find active suppliers
    List<Supplier> findByStatusAndPumpIdOrderBySupplierNameAsc(Supplier.Status status, Long pumpId);
    
    // Search suppliers by multiple criteria
    @Query("SELECT s FROM Supplier s WHERE s.pumpId = :pumpId AND " +
           "(LOWER(s.supplierName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "s.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
           "s.mobile LIKE CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY s.supplierName ASC")
    List<Supplier> searchSuppliers(@Param("pumpId") Long pumpId, @Param("searchTerm") String searchTerm);
    
    // Count suppliers by status
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.status = :status AND s.pumpId = :pumpId")
    Long countByStatusAndPumpId(@Param("status") Supplier.Status status, @Param("pumpId") Long pumpId);
    
    // Find suppliers with credit limit
    @Query("SELECT s FROM Supplier s WHERE s.creditLimit IS NOT NULL AND s.creditLimit > 0 AND s.pumpId = :pumpId ORDER BY s.creditLimit DESC")
    List<Supplier> findSuppliersWithCreditLimit(@Param("pumpId") Long pumpId);
    
    // Find suppliers by payment terms
    List<Supplier> findByPaymentTermsContainingIgnoreCaseOrderBySupplierNameAsc(String paymentTerms);
    List<Supplier> findByPaymentTermsContainingIgnoreCaseAndPumpIdOrderBySupplierNameAsc(String paymentTerms, Long pumpId);
}
