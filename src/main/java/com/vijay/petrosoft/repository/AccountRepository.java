package com.vijay.petrosoft.repository;

import com.vijay.petrosoft.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountCode(String accountCode);
    
    List<Account> findByPumpId(Long pumpId);
    
    List<Account> findByPumpIdAndIsActiveTrue(Long pumpId);
    
    List<Account> findByAccountType(Account.AccountType accountType);
    
    List<Account> findByAccountGroup(Account.AccountGroup accountGroup);
    
    List<Account> findByPumpIdAndAccountType(Long pumpId, Account.AccountType accountType);
    
    List<Account> findByPumpIdAndAccountGroup(Long pumpId, Account.AccountGroup accountGroup);
    
    List<Account> findByParentAccountCode(String parentAccountCode);
    
    @Query("SELECT a FROM Account a WHERE a.pumpId = :pumpId AND a.isActive = true ORDER BY a.accountCode")
    List<Account> findActiveAccountsByPumpIdOrderByCode(@Param("pumpId") Long pumpId);
    
    @Query("SELECT a FROM Account a WHERE a.pumpId = :pumpId AND a.accountType = :accountType AND a.isActive = true ORDER BY a.accountName")
    List<Account> findActiveAccountsByPumpIdAndType(@Param("pumpId") Long pumpId, @Param("accountType") Account.AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.pumpId = :pumpId AND a.accountGroup = :accountGroup AND a.isActive = true ORDER BY a.accountName")
    List<Account> findActiveAccountsByPumpIdAndGroup(@Param("pumpId") Long pumpId, @Param("accountGroup") Account.AccountGroup accountGroup);
    
    @Query("SELECT a FROM Account a WHERE a.accountName LIKE %:name% AND a.pumpId = :pumpId AND a.isActive = true")
    List<Account> findByNameContainingAndPumpId(@Param("name") String name, @Param("pumpId") Long pumpId);
    
    @Query("SELECT a FROM Account a WHERE a.accountCode LIKE %:code% AND a.pumpId = :pumpId AND a.isActive = true")
    List<Account> findByAccountCodeContainingAndPumpId(@Param("code") String code, @Param("pumpId") Long pumpId);
    
    boolean existsByAccountCode(String accountCode);
    
    boolean existsByAccountCodeAndPumpId(String accountCode, Long pumpId);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.pumpId = :pumpId AND a.isActive = true")
    long countActiveAccountsByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.pumpId = :pumpId AND a.accountType = :accountType AND a.isActive = true")
    long countActiveAccountsByPumpIdAndType(@Param("pumpId") Long pumpId, @Param("accountType") Account.AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.isSystemAccount = true AND a.pumpId = :pumpId")
    List<Account> findSystemAccountsByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT a FROM Account a WHERE a.bankAccountNumber IS NOT NULL AND a.pumpId = :pumpId AND a.isActive = true")
    List<Account> findBankAccountsByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT a FROM Account a WHERE a.gstNumber IS NOT NULL AND a.pumpId = :pumpId AND a.isActive = true")
    List<Account> findGstAccountsByPumpId(@Param("pumpId") Long pumpId);
    
    @Query("SELECT a FROM Account a WHERE a.isCash = true AND a.pumpId = :pumpId AND a.isActive = true")
    List<Account> findCashAccountsByPumpId(@Param("pumpId") Long pumpId);
    
    Optional<Account> findByAccountCodeAndPumpId(String accountCode, Long pumpId);
}
