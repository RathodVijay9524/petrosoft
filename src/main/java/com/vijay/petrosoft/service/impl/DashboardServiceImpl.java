package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.dto.DashboardDTO;
import com.vijay.petrosoft.repository.*;
import com.vijay.petrosoft.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final PumpRepository pumpRepository;
    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final FuelTypeRepository fuelTypeRepository;

    @Override
    public DashboardDTO getDashboard(Long pumpId) {
        return DashboardDTO.builder()
                .totalPumps(pumpRepository.count())
                .activePumps(pumpRepository.count()) // Assuming all pumps are active
                .totalCustomers(customerRepository.count())
                .activeCustomers(customerRepository.count())
                .totalSales(saleRepository.count())
                .totalRevenue(getTotalRevenue(pumpId))
                .totalOutstanding(getTotalOutstanding(pumpId))
                .activeSubscriptions(subscriptionRepository.countActiveSubscriptions())
                .expiringSubscriptions((long) subscriptionRepository.findSubscriptionsDueForPayment(LocalDate.now()).size())
                .monthlyRecurringRevenue(getMonthlyRecurringRevenue())
                .totalPayments(paymentRepository.count())
                .totalPaymentAmount(getTotalPaymentAmount())
                .todaySales(getTodaySales(pumpId))
                .thisWeekSales(getThisWeekSales(pumpId))
                .thisMonthSales(getThisMonthSales(pumpId))
                .todayTransactions(getTodayTransactions(pumpId))
                .thisWeekTransactions(getThisWeekTransactions(pumpId))
                .thisMonthTransactions(getThisMonthTransactions(pumpId))
                .fuelTypeSales(getFuelTypeSales(pumpId))
                .fuelTypeTransactions(getFuelTypeTransactions(pumpId))
                .fuelTypeRevenue(getFuelTypeRevenue(pumpId))
                .newCustomersThisMonth(getNewCustomersThisMonth(pumpId))
                .customersWithOutstanding(getCustomersWithOutstanding(pumpId))
                .averageCustomerOutstanding(getAverageCustomerOutstanding(pumpId))
                .customerOutstandingDistribution(getCustomerOutstandingDistribution(pumpId))
                .topPumpsBySales(getTopPumpsBySales(5L))
                .topCustomersByPurchase(getTopCustomersByPurchase(pumpId, 5L))
                .topFuelTypes(getTopFuelTypes(pumpId, 5L))
                .recentSales(getRecentSales(pumpId, 10L))
                .recentPayments(getRecentPayments(10L))
                .recentSubscriptions(getRecentSubscriptions(10L))
                .salesChartData(getSalesChartData(pumpId, "monthly"))
                .revenueChartData(getRevenueChartData(pumpId, "monthly"))
                .customerChartData(getCustomerChartData(pumpId, "monthly"))
                .alerts(getAlerts(pumpId))
                .build();
    }

    @Override
    public DashboardDTO getGlobalDashboard() {
        return getDashboard(null);
    }

    @Override
    public DashboardDTO getDashboardByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate) {
        // Similar to getDashboard but filtered by date range
        return getDashboard(pumpId); // Simplified for now
    }

    @Override
    public Map<String, Object> getOverviewMetrics(Long pumpId) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalPumps", pumpRepository.count());
        metrics.put("totalCustomers", customerRepository.count());
        metrics.put("totalSales", saleRepository.count());
        metrics.put("totalRevenue", getTotalRevenue(pumpId));
        metrics.put("activeSubscriptions", subscriptionRepository.countActiveSubscriptions());
        return metrics;
    }

    @Override
    public Map<String, Object> getOverviewMetricsForDateRange(Long pumpId, LocalDate startDate, LocalDate endDate) {
        return getOverviewMetrics(pumpId); // Simplified for now
    }

    @Override
    public Map<String, Object> getSalesAnalytics(Long pumpId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("todaySales", getTodaySales(pumpId));
        analytics.put("thisWeekSales", getThisWeekSales(pumpId));
        analytics.put("thisMonthSales", getThisMonthSales(pumpId));
        analytics.put("todayTransactions", getTodayTransactions(pumpId));
        analytics.put("thisWeekTransactions", getThisWeekTransactions(pumpId));
        analytics.put("thisMonthTransactions", getThisMonthTransactions(pumpId));
        return analytics;
    }

    @Override
    public Map<String, Object> getSalesAnalyticsByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate) {
        return getSalesAnalytics(pumpId); // Simplified for now
    }

    @Override
    public List<DashboardDTO.SalesChartDTO> getSalesChartData(Long pumpId, String period) {
        List<DashboardDTO.SalesChartDTO> chartData = new ArrayList<>();
        
        // Generate sample data for the last 12 months
        for (int i = 11; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            String periodLabel = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            
            // Generate random sales data (in real implementation, this would come from database)
            BigDecimal sales = BigDecimal.valueOf(Math.random() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP);
            Long transactions = (long) (Math.random() * 1000);
            
            chartData.add(DashboardDTO.SalesChartDTO.builder()
                    .period(periodLabel)
                    .sales(sales)
                    .transactions(transactions)
                    .build());
        }
        
        return chartData;
    }

    @Override
    public Map<String, Object> getRevenueAnalytics(Long pumpId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalRevenue", getTotalRevenue(pumpId));
        analytics.put("monthlyRecurringRevenue", getMonthlyRecurringRevenue());
        analytics.put("totalPayments", paymentRepository.count());
        analytics.put("totalPaymentAmount", getTotalPaymentAmount());
        return analytics;
    }

    @Override
    public List<DashboardDTO.RevenueChartDTO> getRevenueChartData(Long pumpId, String period) {
        List<DashboardDTO.RevenueChartDTO> chartData = new ArrayList<>();
        
        // Generate sample data for the last 12 months
        for (int i = 11; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            String periodLabel = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            
            // Generate random revenue data
            BigDecimal revenue = BigDecimal.valueOf(Math.random() * 150000).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal profit = revenue.multiply(BigDecimal.valueOf(0.2)); // 20% profit margin
            
            chartData.add(DashboardDTO.RevenueChartDTO.builder()
                    .period(periodLabel)
                    .revenue(revenue)
                    .profit(profit)
                    .build());
        }
        
        return chartData;
    }

    @Override
    public Map<String, Object> getCustomerAnalytics(Long pumpId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalCustomers", customerRepository.count());
        analytics.put("newCustomersThisMonth", getNewCustomersThisMonth(pumpId));
        analytics.put("customersWithOutstanding", getCustomersWithOutstanding(pumpId));
        analytics.put("averageCustomerOutstanding", getAverageCustomerOutstanding(pumpId));
        return analytics;
    }

    @Override
    public List<DashboardDTO.CustomerChartDTO> getCustomerChartData(Long pumpId, String period) {
        List<DashboardDTO.CustomerChartDTO> chartData = new ArrayList<>();
        
        // Generate sample data for the last 12 months
        for (int i = 11; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            String periodLabel = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            
            // Generate random customer data
            Long newCustomers = (long) (Math.random() * 50);
            Long totalCustomers = customerRepository.count() + newCustomers;
            
            chartData.add(DashboardDTO.CustomerChartDTO.builder()
                    .period(periodLabel)
                    .newCustomers(newCustomers)
                    .totalCustomers(totalCustomers)
                    .build());
        }
        
        return chartData;
    }

    @Override
    public Map<String, Object> getSubscriptionAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("activeSubscriptions", subscriptionRepository.countActiveSubscriptions());
        analytics.put("expiringSubscriptions", (long) subscriptionRepository.findSubscriptionsDueForPayment(LocalDate.now()).size());
        analytics.put("monthlyRecurringRevenue", getMonthlyRecurringRevenue());
        analytics.put("totalPayments", paymentRepository.count());
        analytics.put("totalPaymentAmount", getTotalPaymentAmount());
        return analytics;
    }

    @Override
    public Map<String, Object> getSubscriptionAnalyticsByDateRange(LocalDate startDate, LocalDate endDate) {
        return getSubscriptionAnalytics(); // Simplified for now
    }

    @Override
    public List<DashboardDTO.TopPumpDTO> getTopPumpsBySales(Long limit) {
        List<DashboardDTO.TopPumpDTO> topPumps = new ArrayList<>();
        
        // Generate sample data (in real implementation, this would come from database)
        for (int i = 1; i <= limit; i++) {
            topPumps.add(DashboardDTO.TopPumpDTO.builder()
                    .pumpId((long) i)
                    .pumpName("Pump " + i)
                    .totalSales(BigDecimal.valueOf(Math.random() * 500000).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .totalTransactions((long) (Math.random() * 5000))
                    .build());
        }
        
        return topPumps;
    }

    @Override
    public List<DashboardDTO.TopCustomerDTO> getTopCustomersByPurchase(Long pumpId, Long limit) {
        List<DashboardDTO.TopCustomerDTO> topCustomers = new ArrayList<>();
        
        // Generate sample data
        for (int i = 1; i <= limit; i++) {
            topCustomers.add(DashboardDTO.TopCustomerDTO.builder()
                    .customerId((long) i)
                    .customerName("Customer " + i)
                    .totalPurchase(BigDecimal.valueOf(Math.random() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .totalTransactions((long) (Math.random() * 500))
                    .build());
        }
        
        return topCustomers;
    }

    @Override
    public List<DashboardDTO.TopFuelTypeDTO> getTopFuelTypes(Long pumpId, Long limit) {
        List<DashboardDTO.TopFuelTypeDTO> topFuelTypes = new ArrayList<>();
        
        // Generate sample data
        String[] fuelTypes = {"Petrol", "Diesel", "CNG", "LPG"};
        for (int i = 0; i < Math.min(limit, fuelTypes.length); i++) {
            topFuelTypes.add(DashboardDTO.TopFuelTypeDTO.builder()
                    .fuelTypeId((long) (i + 1))
                    .fuelTypeName(fuelTypes[i])
                    .totalQuantity(BigDecimal.valueOf(Math.random() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .totalRevenue(BigDecimal.valueOf(Math.random() * 200000).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .build());
        }
        
        return topFuelTypes;
    }

    @Override
    public List<DashboardDTO.RecentSaleDTO> getRecentSales(Long pumpId, Long limit) {
        List<DashboardDTO.RecentSaleDTO> recentSales = new ArrayList<>();
        
        // Generate sample data
        for (int i = 1; i <= limit; i++) {
            recentSales.add(DashboardDTO.RecentSaleDTO.builder()
                    .id((long) i)
                    .pumpId(pumpId != null ? pumpId : (long) (Math.random() * 5 + 1))
                    .customerName("Customer " + i)
                    .fuelType(i % 2 == 0 ? "Petrol" : "Diesel")
                    .amount(BigDecimal.valueOf(Math.random() * 5000).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .transactionDate(LocalDate.now().minusDays(i))
                    .build());
        }
        
        return recentSales;
    }

    @Override
    public List<DashboardDTO.RecentPaymentDTO> getRecentPayments(Long limit) {
        List<DashboardDTO.RecentPaymentDTO> recentPayments = new ArrayList<>();
        
        // Generate sample data
        for (int i = 1; i <= limit; i++) {
            recentPayments.add(DashboardDTO.RecentPaymentDTO.builder()
                    .id((long) i)
                    .subscriptionId((long) (Math.random() * 10 + 1))
                    .amount(BigDecimal.valueOf(Math.random() * 5000).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .status("COMPLETED")
                    .paymentDate(LocalDate.now().minusDays(i))
                    .build());
        }
        
        return recentPayments;
    }

    @Override
    public List<DashboardDTO.RecentSubscriptionDTO> getRecentSubscriptions(Long limit) {
        List<DashboardDTO.RecentSubscriptionDTO> recentSubscriptions = new ArrayList<>();
        
        // Generate sample data
        for (int i = 1; i <= limit; i++) {
            recentSubscriptions.add(DashboardDTO.RecentSubscriptionDTO.builder()
                    .id((long) i)
                    .pumpId((long) (Math.random() * 5 + 1))
                    .planName(i % 3 == 0 ? "Premium Plan" : i % 2 == 0 ? "Basic Plan" : "Enterprise Plan")
                    .status("ACTIVE")
                    .startDate(LocalDate.now().minusDays(i * 10))
                    .build());
        }
        
        return recentSubscriptions;
    }

    @Override
    public List<DashboardDTO.AlertDTO> getAlerts(Long pumpId) {
        List<DashboardDTO.AlertDTO> alerts = new ArrayList<>();
        
        // Generate sample alerts
        alerts.add(DashboardDTO.AlertDTO.builder()
                .type("SUBSCRIPTION")
                .message("Subscription expires in 7 days")
                .severity("WARNING")
                .date(LocalDate.now())
                .build());
        
        alerts.add(DashboardDTO.AlertDTO.builder()
                .type("PAYMENT")
                .message("Payment failed for subscription")
                .severity("ERROR")
                .date(LocalDate.now().minusDays(1))
                .build());
        
        return alerts;
    }

    @Override
    public List<DashboardDTO.AlertDTO> getSystemAlerts() {
        return getAlerts(null);
    }

    @Override
    public Map<String, Object> getRealTimeMetrics(Long pumpId) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("currentHourSales", BigDecimal.valueOf(Math.random() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP));
        metrics.put("currentHourTransactions", (long) (Math.random() * 50));
        metrics.put("onlineUsers", (long) (Math.random() * 20));
        return metrics;
    }

    @Override
    public Map<String, Object> getRealTimeSales(Long pumpId) {
        Map<String, Object> sales = new HashMap<>();
        sales.put("currentSales", BigDecimal.valueOf(Math.random() * 5000).setScale(2, BigDecimal.ROUND_HALF_UP));
        sales.put("currentTransactions", (long) (Math.random() * 25));
        sales.put("lastTransactionTime", LocalDateTime.now().minusMinutes((long) (Math.random() * 30)));
        return sales;
    }

    @Override
    public Map<String, Object> comparePeriods(Long pumpId, LocalDate period1Start, LocalDate period1End, 
                                            LocalDate period2Start, LocalDate period2End) {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("period1Sales", BigDecimal.valueOf(Math.random() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP));
        comparison.put("period2Sales", BigDecimal.valueOf(Math.random() * 120000).setScale(2, BigDecimal.ROUND_HALF_UP));
        comparison.put("growthPercentage", 20.0);
        return comparison;
    }

    @Override
    public Map<String, Object> comparePumps(List<Long> pumpIds, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("pumpComparisons", pumpIds.stream().collect(Collectors.toMap(
                id -> "Pump " + id,
                id -> BigDecimal.valueOf(Math.random() * 200000).setScale(2, BigDecimal.ROUND_HALF_UP)
        )));
        return comparison;
    }

    // Helper methods
    private BigDecimal getTotalRevenue(Long pumpId) {
        return BigDecimal.valueOf(Math.random() * 1000000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getTotalOutstanding(Long pumpId) {
        return BigDecimal.valueOf(Math.random() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getMonthlyRecurringRevenue() {
        return BigDecimal.valueOf(Math.random() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getTotalPaymentAmount() {
        return BigDecimal.valueOf(Math.random() * 200000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getTodaySales(Long pumpId) {
        return BigDecimal.valueOf(Math.random() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getThisWeekSales(Long pumpId) {
        return BigDecimal.valueOf(Math.random() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getThisMonthSales(Long pumpId) {
        return BigDecimal.valueOf(Math.random() * 200000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Long getTodayTransactions(Long pumpId) {
        return (long) (Math.random() * 100);
    }

    private Long getThisWeekTransactions(Long pumpId) {
        return (long) (Math.random() * 500);
    }

    private Long getThisMonthTransactions(Long pumpId) {
        return (long) (Math.random() * 2000);
    }

    private Map<String, BigDecimal> getFuelTypeSales(Long pumpId) {
        Map<String, BigDecimal> sales = new HashMap<>();
        sales.put("Petrol", BigDecimal.valueOf(Math.random() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP));
        sales.put("Diesel", BigDecimal.valueOf(Math.random() * 80000).setScale(2, BigDecimal.ROUND_HALF_UP));
        sales.put("CNG", BigDecimal.valueOf(Math.random() * 20000).setScale(2, BigDecimal.ROUND_HALF_UP));
        return sales;
    }

    private Map<String, Long> getFuelTypeTransactions(Long pumpId) {
        Map<String, Long> transactions = new HashMap<>();
        transactions.put("Petrol", (long) (Math.random() * 1000));
        transactions.put("Diesel", (long) (Math.random() * 1500));
        transactions.put("CNG", (long) (Math.random() * 300));
        return transactions;
    }

    private Map<String, BigDecimal> getFuelTypeRevenue(Long pumpId) {
        Map<String, BigDecimal> revenue = new HashMap<>();
        revenue.put("Petrol", BigDecimal.valueOf(Math.random() * 300000).setScale(2, BigDecimal.ROUND_HALF_UP));
        revenue.put("Diesel", BigDecimal.valueOf(Math.random() * 500000).setScale(2, BigDecimal.ROUND_HALF_UP));
        revenue.put("CNG", BigDecimal.valueOf(Math.random() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP));
        return revenue;
    }

    private Long getNewCustomersThisMonth(Long pumpId) {
        return (long) (Math.random() * 50);
    }

    private Long getCustomersWithOutstanding(Long pumpId) {
        return (long) (Math.random() * 100);
    }

    private BigDecimal getAverageCustomerOutstanding(Long pumpId) {
        return BigDecimal.valueOf(Math.random() * 5000).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Map<String, Long> getCustomerOutstandingDistribution(Long pumpId) {
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("0-1000", (long) (Math.random() * 50));
        distribution.put("1000-5000", (long) (Math.random() * 30));
        distribution.put("5000-10000", (long) (Math.random() * 15));
        distribution.put("10000+", (long) (Math.random() * 5));
        return distribution;
    }
}
