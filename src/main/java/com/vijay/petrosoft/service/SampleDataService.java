package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.*;
import com.vijay.petrosoft.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Service to populate database with sample data for testing and development
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("startup")
public class SampleDataService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PumpRepository pumpRepository;
    private final TankRepository tankRepository;
    private final EmployeeRepository employeeRepository;
    private final VendorRepository vendorRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FinancialYearRepository financialYearRepository;
    private final PaymentRepository paymentRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final PurchaseRepository purchaseRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final ShiftRepository shiftRepository;
    private final AccountRepository accountRepository;
    private final VoucherRepository voucherRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting sample data initialization...");
        
        try {
            // Only create sample data if database is empty
            if (userRepository.count() == 0) {
                createSampleRoles();
                createSampleUsers();
                createSampleEmployees();
                createSampleCustomers();
                createSampleVendors();
                createSampleSubscriptionPlans();
                createSampleFinancialYear();
                createSampleAccounts();
                createSampleVouchers();
                createSampleTanks();
                createSamplePumps();
                createSampleSubscriptions();
                createSamplePayments();
                createSampleSalesTransactions();
                createSamplePurchaseBills();
                createSampleLedgerEntries();
                createSampleShifts();
                
                log.info("✅ Sample data initialization completed successfully!");
                log.info("📊 Created: {} users, {} customers, {} employees, {} vendors, {} roles, {} sales, {} purchases, {} shifts", 
                    userRepository.count(), customerRepository.count(), 
                    employeeRepository.count(), vendorRepository.count(), roleRepository.count(),
                    saleTransactionRepository.count(), purchaseRepository.count(), shiftRepository.count());
            } else {
                log.info("📋 Database already contains data, skipping sample data creation");
            }
        } catch (Exception e) {
            log.error("❌ Error creating sample data: {}", e.getMessage(), e);
        }
    }

    private void createSampleRoles() {
        log.info("Creating sample roles...");
        
        List<Role> roles = Arrays.asList(
            Role.builder()
                .name("ADMIN")
                .roleType(Role.RoleType.OWNER)
                .description("System Administrator with full access")
                .build(),
                
            Role.builder()
                .name("MANAGER")
                .roleType(Role.RoleType.MANAGER)
                .description("Station Manager with management access")
                .build(),
                
            Role.builder()
                .name("OPERATOR")
                .roleType(Role.RoleType.OPERATOR)
                .description("Pump Operator with operational access")
                .build(),
                
            Role.builder()
                .name("ACCOUNTANT")
                .roleType(Role.RoleType.ACCOUNTANT)
                .description("Accountant with financial access")
                .build(),
                
            Role.builder()
                .name("SUPPORT")
                .roleType(Role.RoleType.SUPPORT)
                .description("Support staff with maintenance access")
                .build()
        );
        
        roleRepository.saveAll(roles);
        log.info("✅ Created {} sample roles", roles.size());
    }

    private void createSampleUsers() {
        log.info("Creating sample users...");
        
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role managerRole = roleRepository.findByName("MANAGER").orElse(null);
        Role operatorRole = roleRepository.findByName("OPERATOR").orElse(null);
        Role accountantRole = roleRepository.findByName("ACCOUNTANT").orElse(null);
        
        List<User> users = Arrays.asList(
            User.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .email("admin@petrosoft.com")
                .phone("+91-9876543210")
                .enabled(true)
                .joiningDate(LocalDate.now().minusYears(2))
                .address("Corporate Office, Mumbai")
                .city("Mumbai")
                .state("Maharashtra")
                .pincode("400001")
                .roles(Set.of(adminRole))
                .build(),
                
            User.builder()
                .username("manager")
                .passwordHash(passwordEncoder.encode("manager123"))
                .fullName("John Manager")
                .email("manager@petrosoft.com")
                .phone("+91-9876543211")
                .enabled(true)
                .joiningDate(LocalDate.now().minusMonths(18))
                .address("Station Office, Delhi")
                .city("Delhi")
                .state("Delhi")
                .pincode("110001")
                .roles(Set.of(managerRole))
                .build(),
                
            User.builder()
                .username("operator")
                .passwordHash(passwordEncoder.encode("operator123"))
                .fullName("Mike Operator")
                .email("operator@petrosoft.com")
                .phone("+91-9876543212")
                .enabled(true)
                .joiningDate(LocalDate.now().minusMonths(6))
                .address("Pump Station, Bangalore")
                .city("Bangalore")
                .state("Karnataka")
                .pincode("560001")
                .roles(Set.of(operatorRole))
                .build(),
                
            User.builder()
                .username("cashier")
                .passwordHash(passwordEncoder.encode("cashier123"))
                .fullName("Sarah Cashier")
                .email("cashier@petrosoft.com")
                .phone("+91-9876543213")
                .enabled(true)
                .joiningDate(LocalDate.now().minusMonths(3))
                .address("Cash Counter, Chennai")
                .city("Chennai")
                .state("Tamil Nadu")
                .pincode("600001")
                .roles(Set.of(accountantRole))
                .build(),
                
            User.builder()
                .username("testuser")
                .passwordHash(passwordEncoder.encode("test123"))
                .fullName("Test User")
                .email("test@petrosoft.com")
                .phone("+91-9876543214")
                .enabled(true)
                .joiningDate(LocalDate.now().minusMonths(1))
                .address("Test Location, Hyderabad")
                .city("Hyderabad")
                .state("Telangana")
                .pincode("500001")
                .roles(Set.of(operatorRole))
                .build()
        );
        
        userRepository.saveAll(users);
        log.info("✅ Created {} sample users", users.size());
    }

    private void createSampleEmployees() {
        log.info("Creating sample employees...");
        
        List<Employee> employees = Arrays.asList(
            Employee.builder()
                .employeeCode("EMP001")
                .name("John Manager")
                .role("Station Manager")
                .email("john.manager@petrosoft.com")
                .phone("+91-9876543211")
                .build(),
                
            Employee.builder()
                .employeeCode("EMP002")
                .name("Mike Operator")
                .role("Pump Operator")
                .email("mike.operator@petrosoft.com")
                .phone("+91-9876543212")
                .build(),
                
            Employee.builder()
                .employeeCode("EMP003")
                .name("Sarah Cashier")
                .role("Cashier")
                .email("sarah.cashier@petrosoft.com")
                .phone("+91-9876543213")
                .build(),
                
            Employee.builder()
                .employeeCode("EMP004")
                .name("Rajesh Accountant")
                .role("Accountant")
                .email("rajesh.accountant@petrosoft.com")
                .phone("+91-9876543214")
                .build(),
                
            Employee.builder()
                .employeeCode("EMP005")
                .name("Priya Support")
                .role("Support Staff")
                .email("priya.support@petrosoft.com")
                .phone("+91-9876543215")
                .build()
        );
        
        employeeRepository.saveAll(employees);
        log.info("✅ Created {} sample employees", employees.size());
    }

    private void createSampleCustomers() {
        log.info("Creating sample customers...");
        
        List<Customer> customers = Arrays.asList(
            Customer.builder()
                .code("CUST001")
                .name("Rajesh Kumar")
                .email("rajesh.kumar@email.com")
                .phone("+91-9876543201")
                .address("123 Main Street, Mumbai, Maharashtra 400001")
                .outstanding(new BigDecimal("1500.00"))
                .build(),
                
            Customer.builder()
                .code("CUST002")
                .name("Priya Sharma")
                .email("priya.sharma@email.com")
                .phone("+91-9876543202")
                .address("456 Park Avenue, Delhi, Delhi 110001")
                .outstanding(new BigDecimal("2500.00"))
                .build(),
                
            Customer.builder()
                .code("CUST003")
                .name("Amit Patel")
                .email("amit.patel@email.com")
                .phone("+91-9876543203")
                .address("789 Business District, Bangalore, Karnataka 560001")
                .outstanding(new BigDecimal("3500.00"))
                .build(),
                
            Customer.builder()
                .code("CUST004")
                .name("Sunita Singh")
                .email("sunita.singh@email.com")
                .phone("+91-9876543204")
                .address("321 Garden Road, Chennai, Tamil Nadu 600001")
                .outstanding(new BigDecimal("1000.00"))
                .build(),
                
            Customer.builder()
                .code("CUST005")
                .name("Vikram Gupta")
                .email("vikram.gupta@email.com")
                .phone("+91-9876543205")
                .address("654 Tech Park, Hyderabad, Telangana 500001")
                .outstanding(new BigDecimal("2000.00"))
                .build()
        );
        
        customerRepository.saveAll(customers);
        log.info("✅ Created {} sample customers", customers.size());
    }

    private void createSampleVendors() {
        log.info("Creating sample vendors...");
        
        List<Vendor> vendors = Arrays.asList(
            Vendor.builder()
                .name("Indian Oil Corporation Ltd")
                .email("contact@iocl.com")
                .phone("+91-9876543301")
                .address("Corporate Office, New Delhi")
                .build(),
                
            Vendor.builder()
                .name("Bharat Petroleum Corporation Ltd")
                .email("contact@bharatpetroleum.com")
                .phone("+91-9876543302")
                .address("Mumbai Office, Maharashtra")
                .build(),
                
            Vendor.builder()
                .name("Hindustan Petroleum Corporation Ltd")
                .email("contact@hpcl.com")
                .phone("+91-9876543303")
                .address("Bangalore Office, Karnataka")
                .build(),
                
            Vendor.builder()
                .name("Gilbarco Veeder-Root")
                .email("support@gilbarco.com")
                .phone("+91-9876543304")
                .address("Service Center, Chennai")
                .build(),
                
            Vendor.builder()
                .name("Local Fuel Supplier")
                .email("supplier@local.com")
                .phone("+91-9876543305")
                .address("Local Area, Mumbai")
                .build()
        );
        
        vendorRepository.saveAll(vendors);
        log.info("✅ Created {} sample vendors", vendors.size());
    }

    private void createSampleSubscriptionPlans() {
        log.info("Creating sample subscription plans...");
        
        List<SubscriptionPlan> plans = Arrays.asList(
            SubscriptionPlan.builder()
                .name("Basic Plan")
                .description("Basic fuel station management features")
                .price(99.99)
                .billingCycle(SubscriptionPlan.BillingCycle.MONTHLY)
                .maxPumps(2)
                .maxUsers(5)
                .supportIncluded(false)
                .reportingIncluded(false)
                .apiAccess(false)
                .active(true)
                .build(),
                
            SubscriptionPlan.builder()
                .name("Professional Plan")
                .description("Advanced features with reporting and API access")
                .price(199.99)
                .billingCycle(SubscriptionPlan.BillingCycle.MONTHLY)
                .maxPumps(10)
                .maxUsers(25)
                .supportIncluded(true)
                .reportingIncluded(true)
                .apiAccess(true)
                .active(true)
                .build(),
                
            SubscriptionPlan.builder()
                .name("Enterprise Plan")
                .description("Full-featured solution for large operations")
                .price(399.99)
                .billingCycle(SubscriptionPlan.BillingCycle.MONTHLY)
                .maxPumps(50)
                .maxUsers(100)
                .supportIncluded(true)
                .reportingIncluded(true)
                .apiAccess(true)
                .active(true)
                .build()
        );
        
        subscriptionPlanRepository.saveAll(plans);
        log.info("✅ Created {} sample subscription plans", plans.size());
    }

    private void createSampleFinancialYear() {
        log.info("Creating sample financial year...");
        
        FinancialYear currentYear = FinancialYear.builder()
            .name("FY 2024-25")
            .startDate(LocalDate.of(2024, 4, 1))
            .endDate(LocalDate.of(2025, 3, 31))
            .active(true)
            .description("Current Financial Year 2024-25")
            .build();
        
        financialYearRepository.save(currentYear);
        log.info("✅ Created financial year: {}", currentYear.getName());
    }

    private void createSampleTanks() {
        log.info("Creating sample tanks...");
        
        // Skip tank creation for now as it requires FuelType entity
        log.info("⏭️ Skipping tank creation - requires FuelType entity");
    }

    private void createSamplePumps() {
        log.info("Creating sample pumps...");
        
        // Skip pump creation for now as it might have complex dependencies
        log.info("⏭️ Skipping pump creation - complex dependencies");
    }

    private void createSampleSubscriptions() {
        log.info("Creating sample subscriptions...");
        
        // Skip subscription creation for now as it might have complex dependencies
        log.info("⏭️ Skipping subscription creation - complex dependencies");
    }

    private void createSamplePayments() {
        log.info("Creating sample payments...");
        
        // Get existing customers
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            log.info("⏭️ No customers found, skipping payment creation");
            return;
        }
        
        // Skip payment creation as it requires Subscription entity
        log.info("⏭️ Skipping payment creation - requires Subscription entity");
    }

    private void createSampleSalesTransactions() {
        log.info("Creating sample sales transactions...");
        
        // Get existing customers and employees
        List<Customer> customers = customerRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();
        
        if (customers.isEmpty() || employees.isEmpty()) {
            log.info("⏭️ No customers or employees found, skipping sales transactions");
            return;
        }
        
        List<SaleTransaction> sales = Arrays.asList(
            SaleTransaction.builder()
                .pumpId(1L)
                .customer(customers.get(0))
                .operatorId(employees.get(0).getId())
                .fuelType(null) // Will be set if FuelType entity exists
                .saleNumber("SALE-001-" + System.currentTimeMillis())
                .quantity(new BigDecimal("10.5"))
                .rate(new BigDecimal("95.50"))
                .amount(new BigDecimal("1002.75"))
                .totalAmount(new BigDecimal("1002.75"))
                .paymentMethod(SaleTransaction.PaymentMethod.CASH)
                .saleType(SaleTransaction.SaleType.RETAIL)
                .transactedAt(LocalDateTime.now().minusHours(2))
                .build(),
                
            SaleTransaction.builder()
                .pumpId(1L)
                .customer(customers.size() > 1 ? customers.get(1) : customers.get(0))
                .operatorId(employees.get(0).getId())
                .fuelType(null)
                .saleNumber("SALE-002-" + System.currentTimeMillis())
                .quantity(new BigDecimal("15.0"))
                .rate(new BigDecimal("89.25"))
                .amount(new BigDecimal("1338.75"))
                .totalAmount(new BigDecimal("1338.75"))
                .paymentMethod(SaleTransaction.PaymentMethod.CARD)
                .saleType(SaleTransaction.SaleType.RETAIL)
                .transactedAt(LocalDateTime.now().minusHours(1))
                .build(),
                
            SaleTransaction.builder()
                .pumpId(2L)
                .customer(customers.size() > 2 ? customers.get(2) : customers.get(0))
                .operatorId(employees.size() > 1 ? employees.get(1).getId() : employees.get(0).getId())
                .fuelType(null)
                .saleNumber("SALE-003-" + System.currentTimeMillis())
                .quantity(new BigDecimal("5.0"))
                .rate(new BigDecimal("95.50"))
                .amount(new BigDecimal("477.50"))
                .totalAmount(new BigDecimal("477.50"))
                .paymentMethod(SaleTransaction.PaymentMethod.UPI)
                .saleType(SaleTransaction.SaleType.RETAIL)
                .transactedAt(LocalDateTime.now().minusMinutes(30))
                .build(),
                
            SaleTransaction.builder()
                .pumpId(1L)
                .customer(customers.get(0))
                .operatorId(employees.get(0).getId())
                .fuelType(null)
                .saleNumber("SALE-004-" + System.currentTimeMillis())
                .quantity(new BigDecimal("20.0"))
                .rate(new BigDecimal("95.50"))
                .amount(new BigDecimal("1910.00"))
                .totalAmount(new BigDecimal("1910.00"))
                .paymentMethod(SaleTransaction.PaymentMethod.CREDIT)
                .saleType(SaleTransaction.SaleType.BULK)
                .transactedAt(LocalDateTime.now().minusMinutes(15))
                .build()
        );
        
        saleTransactionRepository.saveAll(sales);
        log.info("✅ Created {} sample sales transactions", sales.size());
    }

    private void createSamplePurchaseBills() {
        log.info("Creating sample purchase bills...");
        
        // Get existing vendors
        List<Vendor> vendors = vendorRepository.findAll();
        
        if (vendors.isEmpty()) {
            log.info("⏭️ No vendors found, skipping purchase bills");
            return;
        }
        
        List<Purchase> purchases = Arrays.asList(
            Purchase.builder()
                .pumpId(1L)
                .vendorId(vendors.get(0).getId())
                .invoiceDate(LocalDate.now().minusDays(5))
                .invoiceNumber("INV-001-" + System.currentTimeMillis())
                .totalAmount(new BigDecimal("50000.00"))
                .build(),
                
            Purchase.builder()
                .pumpId(1L)
                .vendorId(vendors.size() > 1 ? vendors.get(1).getId() : vendors.get(0).getId())
                .invoiceDate(LocalDate.now().minusDays(3))
                .invoiceNumber("INV-002-" + System.currentTimeMillis())
                .totalAmount(new BigDecimal("35000.00"))
                .build(),
                
            Purchase.builder()
                .pumpId(2L)
                .vendorId(vendors.size() > 2 ? vendors.get(2).getId() : vendors.get(0).getId())
                .invoiceDate(LocalDate.now().minusDays(1))
                .invoiceNumber("INV-003-" + System.currentTimeMillis())
                .totalAmount(new BigDecimal("25000.00"))
                .build()
        );
        
        purchaseRepository.saveAll(purchases);
        log.info("✅ Created {} sample purchase bills", purchases.size());
    }

    private void createSampleLedgerEntries() {
        log.info("Creating sample ledger entries...");
        
        // Get existing customers and financial year
        List<Customer> customers = customerRepository.findAll();
        List<FinancialYear> financialYears = financialYearRepository.findAll();
        
        if (customers.isEmpty() || financialYears.isEmpty()) {
            log.info("⏭️ No customers or financial year found, skipping ledger entries");
            return;
        }
        
        List<LedgerEntry> entries = Arrays.asList(
            LedgerEntry.builder()
                .account(null) // Will be set if Account entity exists
                .transactionDate(LocalDate.now().minusDays(2))
                .description("Sale transaction - Customer: " + customers.get(0).getName())
                .voucherNumber("SALE-001")
                .entryType(LedgerEntry.EntryType.DEBIT)
                .debitAmount(new BigDecimal("1002.75"))
                .creditAmount(BigDecimal.ZERO)
                .runningBalance(new BigDecimal("1002.75"))
                .pumpId(1L)
                .partyName(customers.get(0).getName())
                .amount(new BigDecimal("1002.75"))
                .build(),
                
            LedgerEntry.builder()
                .account(null)
                .transactionDate(LocalDate.now().minusDays(1))
                .description("Sale transaction - Customer: " + (customers.size() > 1 ? customers.get(1).getName() : customers.get(0).getName()))
                .voucherNumber("SALE-002")
                .entryType(LedgerEntry.EntryType.DEBIT)
                .debitAmount(new BigDecimal("1338.75"))
                .creditAmount(BigDecimal.ZERO)
                .runningBalance(new BigDecimal("2341.50"))
                .pumpId(1L)
                .partyName(customers.size() > 1 ? customers.get(1).getName() : customers.get(0).getName())
                .amount(new BigDecimal("1338.75"))
                .build(),
                
            LedgerEntry.builder()
                .account(null)
                .transactionDate(LocalDate.now())
                .description("Purchase transaction - Vendor payment")
                .voucherNumber("PUR-001")
                .entryType(LedgerEntry.EntryType.CREDIT)
                .debitAmount(BigDecimal.ZERO)
                .creditAmount(new BigDecimal("50000.00"))
                .runningBalance(new BigDecimal("47658.50"))
                .pumpId(1L)
                .partyName("Fuel Supplier")
                .amount(new BigDecimal("50000.00"))
                .build()
        );
        
        ledgerEntryRepository.saveAll(entries);
        log.info("✅ Created {} sample ledger entries", entries.size());
    }

    private void createSampleShifts() {
        log.info("Creating sample shifts...");
        
        // Get existing employees
        List<Employee> employees = employeeRepository.findAll();
        
        if (employees.isEmpty()) {
            log.info("⏭️ No employees found, skipping shifts");
            return;
        }
        
        List<Shift> shifts = Arrays.asList(
            Shift.builder()
                .pumpId(1L)
                .operatorId(employees.get(0).getId())
                .cashierId(employees.size() > 1 ? employees.get(1).getId() : employees.get(0).getId())
                .shiftName("I")
                .openedAt(LocalDateTime.now().minusHours(8))
                .status(Shift.Status.OPEN)
                .openingCash(new BigDecimal("1000.00"))
                .totalSales(new BigDecimal("2500.00"))
                .cashSales(new BigDecimal("1500.00"))
                .cardSales(new BigDecimal("800.00"))
                .creditSales(new BigDecimal("200.00"))
                .build(),
                
            Shift.builder()
                .pumpId(1L)
                .operatorId(employees.get(0).getId())
                .cashierId(employees.size() > 1 ? employees.get(1).getId() : employees.get(0).getId())
                .shiftName("II")
                .openedAt(LocalDateTime.now().minusHours(4))
                .closedAt(LocalDateTime.now().minusHours(1))
                .status(Shift.Status.CLOSED)
                .openingCash(new BigDecimal("1500.00"))
                .closingCash(new BigDecimal("2800.00"))
                .totalSales(new BigDecimal("1800.00"))
                .cashSales(new BigDecimal("1200.00"))
                .cardSales(new BigDecimal("500.00"))
                .creditSales(new BigDecimal("100.00"))
                .build()
        );
        
        shiftRepository.saveAll(shifts);
        log.info("✅ Created {} sample shifts", shifts.size());
    }

    private void createSampleAccounts() {
        log.info("Creating sample accounts...");
        
        List<Account> accounts = Arrays.asList(
            Account.builder()
                .accountName("Cash Account")
                .accountCode("CASH001")
                .accountType(Account.AccountType.ASSET)
                .description("Main cash account for daily operations")
                .isActive(true)
                .build(),
                
            Account.builder()
                .accountName("Bank Account")
                .accountCode("BANK001")
                .accountType(Account.AccountType.ASSET)
                .description("Primary bank account")
                .isActive(true)
                .build(),
                
            Account.builder()
                .accountName("Sales Revenue")
                .accountCode("SALES001")
                .accountType(Account.AccountType.INCOME)
                .description("Fuel sales revenue account")
                .isActive(true)
                .build(),
                
            Account.builder()
                .accountName("Purchase Account")
                .accountCode("PURCH001")
                .accountType(Account.AccountType.EXPENSE)
                .description("Fuel purchase expenses")
                .isActive(true)
                .build()
        );
        
        accountRepository.saveAll(accounts);
        log.info("✅ Created {} sample accounts", accounts.size());
    }

    private void createSampleVouchers() {
        log.info("Creating sample vouchers...");
        
        List<Voucher> vouchers = Arrays.asList(
            Voucher.builder()
                .voucherNumber("VCH001")
                .voucherDate(LocalDate.now().minusDays(1))
                .narration("Daily sales voucher")
                .voucherType(Voucher.VoucherType.SALES_VOUCHER)
                .totalAmount(new BigDecimal("15000.00"))
                .pumpId(1L)
                .userId(1L)
                .build(),
                
            Voucher.builder()
                .voucherNumber("VCH002")
                .voucherDate(LocalDate.now().minusDays(2))
                .narration("Purchase voucher")
                .voucherType(Voucher.VoucherType.PURCHASE_VOUCHER)
                .totalAmount(new BigDecimal("50000.00"))
                .pumpId(1L)
                .userId(1L)
                .build(),
                
            Voucher.builder()
                .voucherNumber("VCH003")
                .voucherDate(LocalDate.now())
                .narration("Cash receipt voucher")
                .voucherType(Voucher.VoucherType.CUSTOMER_RECEIPT)
                .totalAmount(new BigDecimal("25000.00"))
                .pumpId(1L)
                .userId(1L)
                .build()
        );
        
        voucherRepository.saveAll(vouchers);
        log.info("✅ Created {} sample vouchers", vouchers.size());
    }
}
