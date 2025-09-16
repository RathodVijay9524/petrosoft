package com.vijay.petrosoft.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardDTO {
    
    // Overview Metrics
    private Long totalPumps;
    private Long activePumps;
    private Long totalCustomers;
    private Long activeCustomers;
    private Long totalSales;
    private BigDecimal totalRevenue;
    private BigDecimal totalOutstanding;
    
    // Subscription Metrics
    private Long activeSubscriptions;
    private Long expiringSubscriptions;
    private BigDecimal monthlyRecurringRevenue;
    private Long totalPayments;
    private BigDecimal totalPaymentAmount;
    
    // Sales Metrics
    private BigDecimal todaySales;
    private BigDecimal thisWeekSales;
    private BigDecimal thisMonthSales;
    private Long todayTransactions;
    private Long thisWeekTransactions;
    private Long thisMonthTransactions;
    
    // Fuel Metrics
    private Map<String, BigDecimal> fuelTypeSales;
    private Map<String, Long> fuelTypeTransactions;
    private Map<String, BigDecimal> fuelTypeRevenue;
    
    // Customer Metrics
    private Long newCustomersThisMonth;
    private Long customersWithOutstanding;
    private BigDecimal averageCustomerOutstanding;
    private Map<String, Long> customerOutstandingDistribution;
    
    // Top Performers
    private List<TopPumpDTO> topPumpsBySales;
    private List<TopCustomerDTO> topCustomersByPurchase;
    private List<TopFuelTypeDTO> topFuelTypes;
    
    // Recent Activity
    private List<RecentSaleDTO> recentSales;
    private List<RecentPaymentDTO> recentPayments;
    private List<RecentSubscriptionDTO> recentSubscriptions;
    
    // Charts Data
    private List<SalesChartDTO> salesChartData;
    private List<RevenueChartDTO> revenueChartData;
    private List<CustomerChartDTO> customerChartData;
    
    // Alerts
    private List<AlertDTO> alerts;
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopPumpDTO {
        private Long pumpId;
        private String pumpName;
        private BigDecimal totalSales;
        private Long totalTransactions;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopCustomerDTO {
        private Long customerId;
        private String customerName;
        private BigDecimal totalPurchase;
        private Long totalTransactions;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopFuelTypeDTO {
        private Long fuelTypeId;
        private String fuelTypeName;
        private BigDecimal totalQuantity;
        private BigDecimal totalRevenue;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RecentSaleDTO {
        private Long id;
        private Long pumpId;
        private String customerName;
        private String fuelType;
        private BigDecimal amount;
        private LocalDate transactionDate;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RecentPaymentDTO {
        private Long id;
        private Long subscriptionId;
        private BigDecimal amount;
        private String status;
        private LocalDate paymentDate;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RecentSubscriptionDTO {
        private Long id;
        private Long pumpId;
        private String planName;
        private String status;
        private LocalDate startDate;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SalesChartDTO {
        private String period;
        private BigDecimal sales;
        private Long transactions;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RevenueChartDTO {
        private String period;
        private BigDecimal revenue;
        private BigDecimal profit;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CustomerChartDTO {
        private String period;
        private Long newCustomers;
        private Long totalCustomers;
    }
    
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AlertDTO {
        private String type;
        private String message;
        private String severity;
        private LocalDate date;
    }
}
