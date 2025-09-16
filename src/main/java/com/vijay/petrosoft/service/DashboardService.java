package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.DashboardDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    
    // Main Dashboard Methods
    DashboardDTO getDashboard(Long pumpId);
    DashboardDTO getGlobalDashboard();
    DashboardDTO getDashboardByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    // Overview Metrics
    Map<String, Object> getOverviewMetrics(Long pumpId);
    Map<String, Object> getOverviewMetricsForDateRange(Long pumpId, LocalDate startDate, LocalDate endDate);
    
    // Sales Analytics
    Map<String, Object> getSalesAnalytics(Long pumpId);
    Map<String, Object> getSalesAnalyticsByDateRange(Long pumpId, LocalDate startDate, LocalDate endDate);
    List<DashboardDTO.SalesChartDTO> getSalesChartData(Long pumpId, String period);
    
    // Revenue Analytics
    Map<String, Object> getRevenueAnalytics(Long pumpId);
    List<DashboardDTO.RevenueChartDTO> getRevenueChartData(Long pumpId, String period);
    
    // Customer Analytics
    Map<String, Object> getCustomerAnalytics(Long pumpId);
    List<DashboardDTO.CustomerChartDTO> getCustomerChartData(Long pumpId, String period);
    
    // Subscription Analytics
    Map<String, Object> getSubscriptionAnalytics();
    Map<String, Object> getSubscriptionAnalyticsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Top Performers
    List<DashboardDTO.TopPumpDTO> getTopPumpsBySales(Long limit);
    List<DashboardDTO.TopCustomerDTO> getTopCustomersByPurchase(Long pumpId, Long limit);
    List<DashboardDTO.TopFuelTypeDTO> getTopFuelTypes(Long pumpId, Long limit);
    
    // Recent Activity
    List<DashboardDTO.RecentSaleDTO> getRecentSales(Long pumpId, Long limit);
    List<DashboardDTO.RecentPaymentDTO> getRecentPayments(Long limit);
    List<DashboardDTO.RecentSubscriptionDTO> getRecentSubscriptions(Long limit);
    
    // Alerts and Notifications
    List<DashboardDTO.AlertDTO> getAlerts(Long pumpId);
    List<DashboardDTO.AlertDTO> getSystemAlerts();
    
    // Real-time Data
    Map<String, Object> getRealTimeMetrics(Long pumpId);
    Map<String, Object> getRealTimeSales(Long pumpId);
    
    // Comparative Analytics
    Map<String, Object> comparePeriods(Long pumpId, LocalDate period1Start, LocalDate period1End, 
                                      LocalDate period2Start, LocalDate period2End);
    Map<String, Object> comparePumps(List<Long> pumpIds, LocalDate startDate, LocalDate endDate);
}
