package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.domain.Account;
import com.vijay.petrosoft.dto.AccountDTO;
import com.vijay.petrosoft.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    // CRUD Operations
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            AccountDTO createdAccount = accountService.createAccount(accountDTO);
            log.info("Account created with code: {}", accountDTO.getAccountCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (Exception e) {
            log.error("Error creating account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        Optional<AccountDTO> account = accountService.getAccountById(id);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{accountCode}")
    public ResponseEntity<AccountDTO> getAccountByCode(@PathVariable String accountCode) {
        Optional<AccountDTO> account = accountService.getAccountByCode(accountCode);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByPumpId(@PathVariable Long pumpId) {
        List<AccountDTO> accounts = accountService.getAccountsByPumpId(pumpId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDTO accountDTO) {
        try {
            AccountDTO updatedAccount = accountService.updateAccount(id, accountDTO);
            log.info("Account updated for ID: {}", id);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            log.info("Account deleted for ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Account Type and Group Management
    @GetMapping("/type/{accountType}")
    public ResponseEntity<List<AccountDTO>> getAccountsByType(@PathVariable Account.AccountType accountType) {
        List<AccountDTO> accounts = accountService.getAccountsByType(accountType);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/group/{accountGroup}")
    public ResponseEntity<List<AccountDTO>> getAccountsByGroup(@PathVariable Account.AccountGroup accountGroup) {
        List<AccountDTO> accounts = accountService.getAccountsByGroup(accountGroup);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/pump/{pumpId}/type/{accountType}")
    public ResponseEntity<List<AccountDTO>> getAccountsByPumpIdAndType(
            @PathVariable Long pumpId, 
            @PathVariable Account.AccountType accountType) {
        List<AccountDTO> accounts = accountService.getAccountsByPumpIdAndType(pumpId, accountType);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/pump/{pumpId}/group/{accountGroup}")
    public ResponseEntity<List<AccountDTO>> getAccountsByPumpIdAndGroup(
            @PathVariable Long pumpId, 
            @PathVariable Account.AccountGroup accountGroup) {
        List<AccountDTO> accounts = accountService.getAccountsByPumpIdAndGroup(pumpId, accountGroup);
        return ResponseEntity.ok(accounts);
    }

    // Hierarchical Account Management
    @GetMapping("/children/{parentAccountCode}")
    public ResponseEntity<List<AccountDTO>> getChildAccounts(@PathVariable String parentAccountCode) {
        List<AccountDTO> accounts = accountService.getChildAccounts(parentAccountCode);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/parent")
    public ResponseEntity<AccountDTO> updateParentAccount(
            @PathVariable Long id, 
            @RequestParam String parentAccountCode) {
        try {
            AccountDTO updatedAccount = accountService.updateParentAccount(id, parentAccountCode);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating parent account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Bank Account Management
    @GetMapping("/bank/{pumpId}")
    public ResponseEntity<List<AccountDTO>> getBankAccounts(@PathVariable Long pumpId) {
        List<AccountDTO> accounts = accountService.getBankAccounts(pumpId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/bank")
    public ResponseEntity<AccountDTO> updateBankDetails(
            @PathVariable Long id,
            @RequestParam String bankName,
            @RequestParam String accountNumber,
            @RequestParam String ifscCode,
            @RequestParam String branchName) {
        try {
            AccountDTO updatedAccount = accountService.updateBankDetails(id, bankName, accountNumber, ifscCode, branchName);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating bank details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Tax Account Management
    @GetMapping("/gst/{pumpId}")
    public ResponseEntity<List<AccountDTO>> getGstAccounts(@PathVariable Long pumpId) {
        List<AccountDTO> accounts = accountService.getGstAccounts(pumpId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/tax")
    public ResponseEntity<AccountDTO> updateTaxDetails(
            @PathVariable Long id,
            @RequestParam String gstNumber,
            @RequestParam String panNumber) {
        try {
            AccountDTO updatedAccount = accountService.updateTaxDetails(id, gstNumber, panNumber);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating tax details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Balance Management
    @PutMapping("/{id}/opening")
    public ResponseEntity<AccountDTO> updateOpeningBalance(
            @PathVariable Long id,
            @RequestParam java.math.BigDecimal openingBalance,
            @RequestParam Account.BalanceType balanceType) {
        try {
            AccountDTO updatedAccount = accountService.updateOpeningBalance(id, openingBalance, balanceType);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating opening balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/current")
    public ResponseEntity<AccountDTO> updateCurrentBalance(
            @PathVariable Long id,
            @RequestParam java.math.BigDecimal currentBalance) {
        try {
            AccountDTO updatedAccount = accountService.updateCurrentBalance(id, currentBalance);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating current balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/reconcile")
    public ResponseEntity<AccountDTO> reconcileAccount(
            @PathVariable Long id,
            @RequestParam java.math.BigDecimal reconciledBalance) {
        try {
            AccountDTO updatedAccount = accountService.reconcileAccount(id, reconciledBalance);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error reconciling account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // System Account Management
    @GetMapping("/system/{pumpId}")
    public ResponseEntity<List<AccountDTO>> getSystemAccounts(@PathVariable Long pumpId) {
        List<AccountDTO> accounts = accountService.getSystemAccounts(pumpId);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/system")
    public ResponseEntity<AccountDTO> createSystemAccount(
            @RequestParam Long pumpId,
            @RequestParam String accountName,
            @RequestParam Account.AccountType accountType,
            @RequestParam Account.AccountGroup accountGroup) {
        try {
            AccountDTO createdAccount = accountService.createSystemAccount(pumpId, accountName, accountType, accountGroup);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (Exception e) {
            log.error("Error creating system account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Validation
    @GetMapping("/validate/code/{accountCode}")
    public ResponseEntity<Boolean> validateAccountCode(@PathVariable String accountCode) {
        boolean isValid = accountService.validateAccountCode(accountCode);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/validate/gst/{gstNumber}")
    public ResponseEntity<Boolean> validateGstNumber(@PathVariable String gstNumber) {
        boolean isValid = accountService.validateGstNumber(gstNumber);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/validate/pan/{panNumber}")
    public ResponseEntity<Boolean> validatePanNumber(@PathVariable String panNumber) {
        boolean isValid = accountService.validatePanNumber(panNumber);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/{id}/locked")
    public ResponseEntity<Boolean> isAccountLocked(@PathVariable Long id) {
        boolean isLocked = accountService.isAccountLocked(id);
        return ResponseEntity.ok(isLocked);
    }

    // Search Operations
    @GetMapping("/search/name")
    public ResponseEntity<List<AccountDTO>> searchAccountsByName(
            @RequestParam String name, 
            @RequestParam Long pumpId) {
        List<AccountDTO> accounts = accountService.searchAccountsByName(name, pumpId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/search/code")
    public ResponseEntity<List<AccountDTO>> searchAccountsByCode(
            @RequestParam String code, 
            @RequestParam Long pumpId) {
        List<AccountDTO> accounts = accountService.searchAccountsByCode(code, pumpId);
        return ResponseEntity.ok(accounts);
    }

    // Analytics and Reports
    @GetMapping("/analytics/{pumpId}")
    public ResponseEntity<Map<String, Object>> getAccountAnalytics(@PathVariable Long pumpId) {
        Map<String, Object> analytics = accountService.getAccountAnalytics(pumpId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/count/type/{pumpId}")
    public ResponseEntity<Map<String, Long>> getAccountCountByType(@PathVariable Long pumpId) {
        Map<String, Long> counts = accountService.getAccountCountByType(pumpId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/count/group/{pumpId}")
    public ResponseEntity<Map<String, Long>> getAccountCountByGroup(@PathVariable Long pumpId) {
        Map<String, Long> counts = accountService.getAccountCountByGroup(pumpId);
        return ResponseEntity.ok(counts);
    }

    // Bulk Operations
    @PostMapping("/bulk")
    public ResponseEntity<List<AccountDTO>> bulkCreateAccounts(@Valid @RequestBody List<AccountDTO> accounts) {
        try {
            List<AccountDTO> createdAccounts = accountService.bulkCreateAccounts(accounts);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccounts);
        } catch (Exception e) {
            log.error("Error bulk creating accounts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/bulk/type")
    public ResponseEntity<List<AccountDTO>> bulkUpdateAccountTypes(
            @RequestParam List<Long> accountIds,
            @RequestParam Account.AccountType newType) {
        try {
            List<AccountDTO> updatedAccounts = accountService.bulkUpdateAccountTypes(accountIds, newType);
            return ResponseEntity.ok(updatedAccounts);
        } catch (Exception e) {
            log.error("Error bulk updating account types: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Chart of Accounts
    @GetMapping("/chart/{pumpId}")
    public ResponseEntity<List<AccountDTO>> getChartOfAccounts(@PathVariable Long pumpId) {
        List<AccountDTO> accounts = accountService.getChartOfAccounts(pumpId);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/chart/{pumpId}")
    public ResponseEntity<AccountDTO> createDefaultChartOfAccounts(@PathVariable Long pumpId) {
        try {
            AccountDTO defaultAccount = accountService.createDefaultChartOfAccounts(pumpId);
            return ResponseEntity.status(HttpStatus.CREATED).body(defaultAccount);
        } catch (Exception e) {
            log.error("Error creating default chart of accounts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
