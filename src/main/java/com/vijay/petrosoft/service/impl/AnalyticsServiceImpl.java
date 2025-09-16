package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.repository.*;
import com.vijay.petrosoft.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SaleTransactionRepository saleTransactionRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final EmployeeRepository employeeRepository;
    private final VendorRepository vendorRepository;

    @Override
    public Map<String, Object> getSalesAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic sales metrics
        analytics.put("totalSales", saleTransactionRepository.count());
        analytics.put("totalRevenue", getTotalSalesRevenue());
        analytics.put("averageTransactionValue", getAverageTransactionValue());
        analytics.put("salesToday", getSalesToday());
        analytics.put("salesThisWeek", getSalesThisWeek());
        analytics.put("salesThisMonth", getSalesThisMonth());
        
        // Sales trends
        analytics.put("dailySales", getDailySalesTrend());
        analytics.put("monthlySales", getMonthlySalesTrend());
        
        // Top performing metrics
        analytics.put("topCustomers", getTopCustomersBySales());
        analytics.put("salesByPaymentMethod", getSalesByPaymentMethod());
        
        return analytics;
    }

    @Override
    public Map<String, Object> getSalesAnalyticsByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Sales in date range
        analytics.put("totalSales", saleTransactionRepository.findByTransactedAtBetween(startDateTime, endDateTime).size());
        analytics.put("totalRevenue", getSalesRevenueByDateRange(startDateTime, endDateTime));
        analytics.put("averageTransactionValue", getAverageTransactionValueByDateRange(startDateTime, endDateTime));
        
        return analytics;
    }

    @Override
    public Map<String, Object> getCustomerAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic customer metrics
        analytics.put("totalCustomers", customerRepository.count());
        analytics.put("newCustomersToday", getNewCustomersToday());
        analytics.put("newCustomersThisWeek", getNewCustomersThisWeek());
        analytics.put("newCustomersThisMonth", getNewCustomersThisMonth());
        
        // Customer behavior
        analytics.put("activeCustomers", getActiveCustomers());
        analytics.put("customerRetentionRate", getCustomerRetentionRate());
        analytics.put("averageCustomerValue", getAverageCustomerValue());
        
        // Customer segmentation
        analytics.put("customerSegments", getCustomerSegments());
        analytics.put("outstandingAmount", getTotalOutstandingAmount());
        
        return analytics;
    }

    @Override
    public Map<String, Object> getCustomerAnalyticsByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Customers in date range
        analytics.put("newCustomers", customerRepository.countByCreatedAtBetween(startDateTime, endDateTime));
        analytics.put("activeCustomers", getActiveCustomersByDateRange(startDateTime, endDateTime));
        
        return analytics;
    }

    @Override
    public Map<String, Object> getFinancialAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Revenue metrics
        analytics.put("totalRevenue", getTotalRevenue());
        analytics.put("revenueToday", getRevenueToday());
        analytics.put("revenueThisWeek", getRevenueThisWeek());
        analytics.put("revenueThisMonth", getRevenueThisMonth());
        
        // Payment metrics
        analytics.put("totalPayments", paymentRepository.count());
        analytics.put("totalPaymentAmount", getTotalPaymentAmount());
        analytics.put("averagePaymentAmount", getAveragePaymentAmount());
        
        // Financial health
        analytics.put("outstandingReceivables", getOutstandingReceivables());
        analytics.put("paymentSuccessRate", getPaymentSuccessRate());
        
        // Ledger metrics
        analytics.put("totalLedgerEntries", ledgerEntryRepository.count());
        analytics.put("unreconciledEntries", getUnreconciledEntries());
        
        return analytics;
    }

    @Override
    public Map<String, Object> getFinancialAnalyticsByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Financial metrics in date range
        analytics.put("revenue", getRevenueByDateRange(startDateTime, endDateTime));
        analytics.put("payments", getPaymentsByDateRange(startDateTime, endDateTime));
        analytics.put("ledgerEntries", getLedgerEntriesByDateRange(startDateTime, endDateTime));
        
        return analytics;
    }

    @Override
    public Map<String, Object> getEmployeeAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic employee metrics
        analytics.put("totalEmployees", employeeRepository.count());
        analytics.put("activeEmployees", getActiveEmployees());
        analytics.put("newEmployeesThisMonth", getNewEmployeesThisMonth());
        
        // Performance metrics
        analytics.put("topPerformingEmployees", getTopPerformingEmployees());
        analytics.put("employeeProductivity", getEmployeeProductivity());
        
        return analytics;
    }

    @Override
    public Map<String, Object> getVendorAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic vendor metrics
        analytics.put("totalVendors", vendorRepository.count());
        analytics.put("activeVendors", getActiveVendors());
        analytics.put("newVendorsThisMonth", getNewVendorsThisMonth());
        
        // Vendor performance
        analytics.put("topVendors", getTopVendorsByPurchases());
        analytics.put("totalOutstandingPayables", getTotalOutstandingPayables());
        
        return analytics;
    }

    // Helper methods for calculations
    private BigDecimal getTotalSalesRevenue() {
        // Simplified calculation - in real scenario, sum from sales transactions
        return BigDecimal.valueOf(Math.random() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getAverageTransactionValue() {
        return BigDecimal.valueOf(Math.random() * 1000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Long getSalesToday() {
        return (long) (Math.random() * 50);
    }

    private Long getSalesThisWeek() {
        return (long) (Math.random() * 300);
    }

    private Long getSalesThisMonth() {
        return (long) (Math.random() * 1200);
    }

    private Map<String, Object> getDailySalesTrend() {
        Map<String, Object> trend = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            trend.put(LocalDate.now().minusDays(i).toString(), (long) (Math.random() * 100));
        }
        return trend;
    }

    private Map<String, Object> getMonthlySalesTrend() {
        Map<String, Object> trend = new HashMap<>();
        for (int i = 11; i >= 0; i--) {
            trend.put(LocalDate.now().minusMonths(i).toString().substring(0, 7), (long) (Math.random() * 1000));
        }
        return trend;
    }

    private Map<String, Object> getTopCustomersBySales() {
        Map<String, Object> topCustomers = new HashMap<>();
        topCustomers.put("customer1", Map.of("name", "Customer 1", "sales", (long) (Math.random() * 10000)));
        topCustomers.put("customer2", Map.of("name", "Customer 2", "sales", (long) (Math.random() * 8000)));
        topCustomers.put("customer3", Map.of("name", "Customer 3", "sales", (long) (Math.random() * 6000)));
        return topCustomers;
    }

    private Map<String, Long> getSalesByPaymentMethod() {
        Map<String, Long> paymentMethods = new HashMap<>();
        paymentMethods.put("Cash", (long) (Math.random() * 100));
        paymentMethods.put("Card", (long) (Math.random() * 80));
        paymentMethods.put("UPI", (long) (Math.random() * 60));
        paymentMethods.put("Credit", (long) (Math.random() * 40));
        return paymentMethods;
    }

    private Long getNewCustomersToday() {
        return (long) (Math.random() * 10);
    }

    private Long getNewCustomersThisWeek() {
        return (long) (Math.random() * 50);
    }

    private Long getNewCustomersThisMonth() {
        return (long) (Math.random() * 200);
    }

    private Long getActiveCustomers() {
        return customerRepository.count();
    }

    private Double getCustomerRetentionRate() {
        return 85.0 + Math.random() * 10;
    }

    private BigDecimal getAverageCustomerValue() {
        return BigDecimal.valueOf(Math.random() * 5000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Map<String, Object> getCustomerSegments() {
        Map<String, Object> segments = new HashMap<>();
        segments.put("Premium", (long) (Math.random() * 20));
        segments.put("Regular", (long) (Math.random() * 100));
        segments.put("New", (long) (Math.random() * 50));
        return segments;
    }

    private BigDecimal getTotalOutstandingAmount() {
        return BigDecimal.valueOf(Math.random() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getTotalRevenue() {
        return BigDecimal.valueOf(Math.random() * 200000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getRevenueToday() {
        return BigDecimal.valueOf(Math.random() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getRevenueThisWeek() {
        return BigDecimal.valueOf(Math.random() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getRevenueThisMonth() {
        return BigDecimal.valueOf(Math.random() * 200000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getTotalPaymentAmount() {
        return BigDecimal.valueOf(Math.random() * 150000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getAveragePaymentAmount() {
        return BigDecimal.valueOf(Math.random() * 2000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getOutstandingReceivables() {
        return BigDecimal.valueOf(Math.random() * 30000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Double getPaymentSuccessRate() {
        return 95.0 + Math.random() * 5;
    }

    private Long getUnreconciledEntries() {
        return (long) (Math.random() * 100);
    }

    private Long getActiveEmployees() {
        return employeeRepository.count();
    }

    private Long getNewEmployeesThisMonth() {
        return (long) (Math.random() * 5);
    }

    private Map<String, Object> getTopPerformingEmployees() {
        Map<String, Object> employees = new HashMap<>();
        employees.put("employee1", Map.of("name", "Employee 1", "performance", 95.0));
        employees.put("employee2", Map.of("name", "Employee 2", "performance", 90.0));
        employees.put("employee3", Map.of("name", "Employee 3", "performance", 85.0));
        return employees;
    }

    private Map<String, Object> getEmployeeProductivity() {
        Map<String, Object> productivity = new HashMap<>();
        productivity.put("averageSalesPerEmployee", (long) (Math.random() * 1000));
        productivity.put("totalHoursWorked", (long) (Math.random() * 2000));
        productivity.put("efficiency", 85.0 + Math.random() * 10);
        return productivity;
    }

    private Long getActiveVendors() {
        return vendorRepository.count();
    }

    private Long getNewVendorsThisMonth() {
        return (long) (Math.random() * 3);
    }

    private Map<String, Object> getTopVendorsByPurchases() {
        Map<String, Object> vendors = new HashMap<>();
        vendors.put("vendor1", Map.of("name", "Vendor 1", "purchases", (long) (Math.random() * 50000)));
        vendors.put("vendor2", Map.of("name", "Vendor 2", "purchases", (long) (Math.random() * 40000)));
        vendors.put("vendor3", Map.of("name", "Vendor 3", "purchases", (long) (Math.random() * 30000)));
        return vendors;
    }

    private BigDecimal getTotalOutstandingPayables() {
        return BigDecimal.valueOf(Math.random() * 25000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Date range helper methods
    private BigDecimal getSalesRevenueByDateRange(LocalDateTime start, LocalDateTime end) {
        return BigDecimal.valueOf(Math.random() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getAverageTransactionValueByDateRange(LocalDateTime start, LocalDateTime end) {
        return BigDecimal.valueOf(Math.random() * 1000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Long getActiveCustomersByDateRange(LocalDateTime start, LocalDateTime end) {
        return (long) (Math.random() * 50);
    }

    private BigDecimal getRevenueByDateRange(LocalDateTime start, LocalDateTime end) {
        return BigDecimal.valueOf(Math.random() * 30000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Long getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return (long) (Math.random() * 100);
    }

    private Long getLedgerEntriesByDateRange(LocalDateTime start, LocalDateTime end) {
        return (long) (Math.random() * 200);
    }
}
