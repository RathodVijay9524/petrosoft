package com.vijay.petrosoft.service;

import java.time.LocalDate;
import java.util.Map;

public interface BusinessIntelligenceService {
    
    // Revenue Trends
    Map<String, Object> getRevenueTrends();
    Map<String, Object> getRevenueTrendsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Customer Insights
    Map<String, Object> getCustomerInsights();
    Map<String, Object> getCustomerInsightsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Performance Metrics
    Map<String, Object> getPerformanceMetrics();
    Map<String, Object> getPerformanceMetricsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Operational Efficiency
    Map<String, Object> getOperationalEfficiency();
    Map<String, Object> getOperationalEfficiencyByDateRange(LocalDate startDate, LocalDate endDate);
}
