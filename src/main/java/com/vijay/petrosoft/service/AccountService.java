package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.AccountDTO;
import com.vijay.petrosoft.domain.Account;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountService {
    
    // CRUD Operations
    AccountDTO createAccount(AccountDTO accountDTO);
    AccountDTO updateAccount(Long id, AccountDTO accountDTO);
    Optional<AccountDTO> getAccountById(Long id);
    Optional<AccountDTO> getAccountByCode(String accountCode);
    List<AccountDTO> getAllAccounts();
    List<AccountDTO> getAccountsByPumpId(Long pumpId);
    void deleteAccount(Long id);
    
    // Account Type and Group Management
    List<AccountDTO> getAccountsByType(Account.AccountType accountType);
    List<AccountDTO> getAccountsByGroup(Account.AccountGroup accountGroup);
    List<AccountDTO> getAccountsByPumpIdAndType(Long pumpId, Account.AccountType accountType);
    List<AccountDTO> getAccountsByPumpIdAndGroup(Long pumpId, Account.AccountGroup accountGroup);
    
    // Hierarchical Account Management
    List<AccountDTO> getChildAccounts(String parentAccountCode);
    AccountDTO updateParentAccount(Long id, String parentAccountCode);
    
    // Bank Account Management
    List<AccountDTO> getBankAccounts(Long pumpId);
    AccountDTO updateBankDetails(Long id, String bankName, String accountNumber, String ifscCode, String branchName);
    
    // Tax Account Management
    List<AccountDTO> getGstAccounts(Long pumpId);
    AccountDTO updateTaxDetails(Long id, String gstNumber, String panNumber);
    
    // Balance Management
    AccountDTO updateOpeningBalance(Long id, java.math.BigDecimal openingBalance, Account.BalanceType balanceType);
    AccountDTO updateCurrentBalance(Long id, java.math.BigDecimal currentBalance);
    AccountDTO reconcileAccount(Long id, java.math.BigDecimal reconciledBalance);
    
    // System Account Management
    List<AccountDTO> getSystemAccounts(Long pumpId);
    AccountDTO createSystemAccount(Long pumpId, String accountName, Account.AccountType accountType, Account.AccountGroup accountGroup);
    
    // Account Validation
    boolean validateAccountCode(String accountCode);
    boolean validateGstNumber(String gstNumber);
    boolean validatePanNumber(String panNumber);
    boolean isAccountLocked(Long id);
    
    // Search and Filter
    List<AccountDTO> searchAccountsByName(String name, Long pumpId);
    List<AccountDTO> searchAccountsByCode(String code, Long pumpId);
    
    // Analytics and Reports
    Map<String, Object> getAccountAnalytics(Long pumpId);
    Map<String, Long> getAccountCountByType(Long pumpId);
    Map<String, Long> getAccountCountByGroup(Long pumpId);
    
    // Bulk Operations
    List<AccountDTO> bulkCreateAccounts(List<AccountDTO> accounts);
    List<AccountDTO> bulkUpdateAccountTypes(List<Long> accountIds, Account.AccountType newType);
    
    // Chart of Accounts
    List<AccountDTO> getChartOfAccounts(Long pumpId);
    AccountDTO createDefaultChartOfAccounts(Long pumpId);
}
