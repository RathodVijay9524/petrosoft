package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Account;
import com.vijay.petrosoft.dto.AccountDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.vijay.petrosoft.repository.AccountRepository;
import com.vijay.petrosoft.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    // GST Number Pattern: 22ABCDE1234F1Z5
    private static final Pattern GST_PATTERN = Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$");
    
    // PAN Number Pattern: ABCDE1234F
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {
        // Check if account code already exists
        if (accountRepository.existsByAccountCodeAndPumpId(accountDTO.getAccountCode(), accountDTO.getPumpId())) {
            throw new DuplicateResourceException("Account code already exists: " + accountDTO.getAccountCode());
        }

        // Validate GST and PAN if provided
        if (accountDTO.getGstNumber() != null && !validateGstNumber(accountDTO.getGstNumber())) {
            throw new IllegalArgumentException("Invalid GST number format");
        }

        if (accountDTO.getPanNumber() != null && !validatePanNumber(accountDTO.getPanNumber())) {
            throw new IllegalArgumentException("Invalid PAN number format");
        }

        Account account = Account.builder()
                .accountCode(accountDTO.getAccountCode())
                .accountName(accountDTO.getAccountName())
                .accountType(accountDTO.getAccountType())
                .accountGroup(accountDTO.getAccountGroup())
                .description(accountDTO.getDescription())
                .openingBalance(accountDTO.getOpeningBalance())
                .balanceType(accountDTO.getBalanceType())
                .pumpId(accountDTO.getPumpId())
                .parentAccountCode(accountDTO.getParentAccountCode())
                .gstNumber(accountDTO.getGstNumber())
                .panNumber(accountDTO.getPanNumber())
                .bankName(accountDTO.getBankName())
                .bankAccountNumber(accountDTO.getBankAccountNumber())
                .ifscCode(accountDTO.getIfscCode())
                .branchName(accountDTO.getBranchName())
                .isSystemAccount(accountDTO.isSystemAccount())
                .isActive(accountDTO.isActive())
                .isLocked(accountDTO.isLocked())
                .currentBalance(accountDTO.getCurrentBalance())
                .reconciledBalance(accountDTO.getReconciledBalance())
                .lastReconciledAt(accountDTO.getLastReconciledAt())
                .additionalInfo(accountDTO.getAdditionalInfo() != null ? 
                    accountDTO.getAdditionalInfo().toString() : null)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account created with code: {}", accountDTO.getAccountCode());
        
        return convertToDTO(savedAccount);
    }

    @Override
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (account.isLocked()) {
            throw new IllegalStateException("Cannot update locked account");
        }

        // Validate GST and PAN if provided
        if (accountDTO.getGstNumber() != null && !validateGstNumber(accountDTO.getGstNumber())) {
            throw new IllegalArgumentException("Invalid GST number format");
        }

        if (accountDTO.getPanNumber() != null && !validatePanNumber(accountDTO.getPanNumber())) {
            throw new IllegalArgumentException("Invalid PAN number format");
        }

        // Update all fields
        account.setAccountCode(accountDTO.getAccountCode());
        account.setAccountName(accountDTO.getAccountName());
        account.setAccountType(accountDTO.getAccountType());
        account.setAccountGroup(accountDTO.getAccountGroup());
        account.setDescription(accountDTO.getDescription());
        account.setOpeningBalance(accountDTO.getOpeningBalance());
        account.setBalanceType(accountDTO.getBalanceType());
        account.setParentAccountCode(accountDTO.getParentAccountCode());
        account.setGstNumber(accountDTO.getGstNumber());
        account.setPanNumber(accountDTO.getPanNumber());
        account.setBankName(accountDTO.getBankName());
        account.setBankAccountNumber(accountDTO.getBankAccountNumber());
        account.setIfscCode(accountDTO.getIfscCode());
        account.setBranchName(accountDTO.getBranchName());
        account.setSystemAccount(accountDTO.isSystemAccount());
        account.setActive(accountDTO.isActive());
        account.setLocked(accountDTO.isLocked());
        account.setCurrentBalance(accountDTO.getCurrentBalance());
        account.setReconciledBalance(accountDTO.getReconciledBalance());
        account.setLastReconciledAt(accountDTO.getLastReconciledAt());
        account.setAdditionalInfo(accountDTO.getAdditionalInfo() != null ? 
            accountDTO.getAdditionalInfo().toString() : null);

        Account updatedAccount = accountRepository.save(account);
        log.info("Account updated for ID: {}", id);
        
        return convertToDTO(updatedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountDTO> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountDTO> getAccountByCode(String accountCode) {
        return accountRepository.findByAccountCode(accountCode)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByPumpId(Long pumpId) {
        return accountRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (account.isSystemAccount()) {
            throw new IllegalStateException("Cannot delete system account");
        }

        if (account.isLocked()) {
            throw new IllegalStateException("Cannot delete locked account");
        }

        accountRepository.deleteById(id);
        log.info("Account deleted for ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByType(Account.AccountType accountType) {
        return accountRepository.findByAccountType(accountType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByGroup(Account.AccountGroup accountGroup) {
        return accountRepository.findByAccountGroup(accountGroup).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByPumpIdAndType(Long pumpId, Account.AccountType accountType) {
        return accountRepository.findActiveAccountsByPumpIdAndType(pumpId, accountType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByPumpIdAndGroup(Long pumpId, Account.AccountGroup accountGroup) {
        return accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, accountGroup).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getChildAccounts(String parentAccountCode) {
        return accountRepository.findByParentAccountCode(parentAccountCode).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO updateParentAccount(Long id, String parentAccountCode) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setParentAccountCode(parentAccountCode);
        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getBankAccounts(Long pumpId) {
        return accountRepository.findBankAccountsByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO updateBankDetails(Long id, String bankName, String accountNumber, String ifscCode, String branchName) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setBankName(bankName);
        account.setBankAccountNumber(accountNumber);
        account.setIfscCode(ifscCode);
        account.setBranchName(branchName);

        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getGstAccounts(Long pumpId) {
        return accountRepository.findGstAccountsByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO updateTaxDetails(Long id, String gstNumber, String panNumber) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (gstNumber != null && !validateGstNumber(gstNumber)) {
            throw new IllegalArgumentException("Invalid GST number format");
        }

        if (panNumber != null && !validatePanNumber(panNumber)) {
            throw new IllegalArgumentException("Invalid PAN number format");
        }

        account.setGstNumber(gstNumber);
        account.setPanNumber(panNumber);

        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Override
    public AccountDTO updateOpeningBalance(Long id, BigDecimal openingBalance, Account.BalanceType balanceType) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setOpeningBalance(openingBalance);
        account.setBalanceType(balanceType);

        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Override
    public AccountDTO updateCurrentBalance(Long id, BigDecimal currentBalance) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setCurrentBalance(currentBalance);

        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Override
    public AccountDTO reconcileAccount(Long id, BigDecimal reconciledBalance) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setReconciledBalance(reconciledBalance);
        account.setLastReconciledAt(LocalDateTime.now());

        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getSystemAccounts(Long pumpId) {
        return accountRepository.findSystemAccountsByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO createSystemAccount(Long pumpId, String accountName, Account.AccountType accountType, Account.AccountGroup accountGroup) {
        String accountCode = generateSystemAccountCode(accountType, accountGroup);
        
        AccountDTO accountDTO = AccountDTO.builder()
                .accountCode(accountCode)
                .accountName(accountName)
                .accountType(accountType)
                .accountGroup(accountGroup)
                .pumpId(pumpId)
                .isSystemAccount(true)
                .isActive(true)
                .isLocked(false)
                .openingBalance(BigDecimal.ZERO)
                .currentBalance(BigDecimal.ZERO)
                .reconciledBalance(BigDecimal.ZERO)
                .balanceType(Account.BalanceType.DEBIT)
                .build();

        return createAccount(accountDTO);
    }

    @Override
    public boolean validateAccountCode(String accountCode) {
        return accountCode != null && accountCode.matches("^[A-Z0-9]{2,20}$");
    }

    @Override
    public boolean validateGstNumber(String gstNumber) {
        return gstNumber != null && GST_PATTERN.matcher(gstNumber).matches();
    }

    @Override
    public boolean validatePanNumber(String panNumber) {
        return panNumber != null && PAN_PATTERN.matcher(panNumber).matches();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountLocked(Long id) {
        return accountRepository.findById(id)
                .map(Account::isLocked)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> searchAccountsByName(String name, Long pumpId) {
        return accountRepository.findByNameContainingAndPumpId(name, pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> searchAccountsByCode(String code, Long pumpId) {
        return accountRepository.findByAccountCodeContainingAndPumpId(code, pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAccountAnalytics(Long pumpId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalAccounts", accountRepository.countActiveAccountsByPumpId(pumpId));
        analytics.put("systemAccounts", accountRepository.findSystemAccountsByPumpId(pumpId).size());
        analytics.put("bankAccounts", accountRepository.findBankAccountsByPumpId(pumpId).size());
        analytics.put("gstAccounts", accountRepository.findGstAccountsByPumpId(pumpId).size());
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAccountCountByType(Long pumpId) {
        Map<String, Long> counts = new HashMap<>();
        for (Account.AccountType type : Account.AccountType.values()) {
            counts.put(type.name(), accountRepository.countActiveAccountsByPumpIdAndType(pumpId, type));
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAccountCountByGroup(Long pumpId) {
        Map<String, Long> counts = new HashMap<>();
        for (Account.AccountGroup group : Account.AccountGroup.values()) {
            counts.put(group.name(), (long) accountRepository.findActiveAccountsByPumpIdAndGroup(pumpId, group).size());
        }
        return counts;
    }

    @Override
    public List<AccountDTO> bulkCreateAccounts(List<AccountDTO> accounts) {
        List<AccountDTO> createdAccounts = new ArrayList<>();
        
        for (AccountDTO accountDTO : accounts) {
            try {
                AccountDTO createdAccount = createAccount(accountDTO);
                createdAccounts.add(createdAccount);
            } catch (Exception e) {
                log.error("Failed to create account: {}", accountDTO.getAccountCode(), e);
            }
        }
        
        return createdAccounts;
    }

    @Override
    public List<AccountDTO> bulkUpdateAccountTypes(List<Long> accountIds, Account.AccountType newType) {
        List<AccountDTO> updatedAccounts = new ArrayList<>();
        
        for (Long accountId : accountIds) {
            try {
                Account account = accountRepository.findById(accountId).orElse(null);
                if (account != null && !account.isLocked()) {
                    account.setAccountType(newType);
                    Account updatedAccount = accountRepository.save(account);
                    updatedAccounts.add(convertToDTO(updatedAccount));
                }
            } catch (Exception e) {
                log.error("Failed to update account type for ID: {}", accountId, e);
            }
        }
        
        return updatedAccounts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getChartOfAccounts(Long pumpId) {
        return accountRepository.findActiveAccountsByPumpIdOrderByCode(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO createDefaultChartOfAccounts(Long pumpId) {
        // Create basic chart of accounts structure
        List<AccountDTO> defaultAccounts = Arrays.asList(
            // Assets
            AccountDTO.builder().accountCode("CASH").accountName("Cash").accountType(Account.AccountType.ASSET).accountGroup(Account.AccountGroup.CURRENT_ASSETS).pumpId(pumpId).isSystemAccount(true).build(),
            AccountDTO.builder().accountCode("BANK").accountName("Bank").accountType(Account.AccountType.ASSET).accountGroup(Account.AccountGroup.CURRENT_ASSETS).pumpId(pumpId).isSystemAccount(true).build(),
            AccountDTO.builder().accountCode("INVENTORY").accountName("Inventory").accountType(Account.AccountType.ASSET).accountGroup(Account.AccountGroup.CURRENT_ASSETS).pumpId(pumpId).isSystemAccount(true).build(),
            
            // Liabilities
            AccountDTO.builder().accountCode("ACCOUNTS_PAYABLE").accountName("Accounts Payable").accountType(Account.AccountType.LIABILITY).accountGroup(Account.AccountGroup.CURRENT_LIABILITIES).pumpId(pumpId).isSystemAccount(true).build(),
            
            // Equity
            AccountDTO.builder().accountCode("CAPITAL").accountName("Capital").accountType(Account.AccountType.EQUITY).accountGroup(Account.AccountGroup.CAPITAL).pumpId(pumpId).isSystemAccount(true).build(),
            
            // Income
            AccountDTO.builder().accountCode("SALES").accountName("Sales").accountType(Account.AccountType.INCOME).accountGroup(Account.AccountGroup.DIRECT_INCOME).pumpId(pumpId).isSystemAccount(true).build(),
            
            // Expenses
            AccountDTO.builder().accountCode("PURCHASES").accountName("Purchases").accountType(Account.AccountType.EXPENSE).accountGroup(Account.AccountGroup.DIRECT_EXPENSES).pumpId(pumpId).isSystemAccount(true).build(),
            AccountDTO.builder().accountCode("OPERATING_EXPENSES").accountName("Operating Expenses").accountType(Account.AccountType.EXPENSE).accountGroup(Account.AccountGroup.INDIRECT_EXPENSES).pumpId(pumpId).isSystemAccount(true).build()
        );

        List<AccountDTO> createdAccounts = bulkCreateAccounts(defaultAccounts);
        log.info("Created {} default accounts for pump ID: {}", createdAccounts.size(), pumpId);
        
        return createdAccounts.isEmpty() ? null : createdAccounts.get(0);
    }

    private String generateSystemAccountCode(Account.AccountType accountType, Account.AccountGroup accountGroup) {
        String prefix = accountType.name().substring(0, 3);
        String suffix = accountGroup.name().substring(0, 3);
        return prefix + "_" + suffix + "_" + System.currentTimeMillis() % 1000;
    }

    private AccountDTO convertToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .accountCode(account.getAccountCode())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .accountGroup(account.getAccountGroup())
                .description(account.getDescription())
                .openingBalance(account.getOpeningBalance())
                .balanceType(account.getBalanceType())
                .pumpId(account.getPumpId())
                .parentAccountCode(account.getParentAccountCode())
                .gstNumber(account.getGstNumber())
                .panNumber(account.getPanNumber())
                .bankName(account.getBankName())
                .bankAccountNumber(account.getBankAccountNumber())
                .ifscCode(account.getIfscCode())
                .branchName(account.getBranchName())
                .isSystemAccount(account.isSystemAccount())
                .isActive(account.isActive())
                .isLocked(account.isLocked())
                .currentBalance(account.getCurrentBalance())
                .reconciledBalance(account.getReconciledBalance())
                .lastReconciledAt(account.getLastReconciledAt())
                .build();
    }
}
