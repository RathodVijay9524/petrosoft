package com.vijay.petrosoft.service;

import java.time.LocalDate;
import java.util.Map;

public interface AnalyticsService {
    
    // Sales Analytics
    Map<String, Object> getSalesAnalytics();
    Map<String, Object> getSalesAnalyticsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Customer Analytics
    Map<String, Object> getCustomerAnalytics();
    Map<String, Object> getCustomerAnalyticsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Financial Analytics
    Map<String, Object> getFinancialAnalytics();
    Map<String, Object> getFinancialAnalyticsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Employee Analytics
    Map<String, Object> getEmployeeAnalytics();
    
    // Vendor Analytics
    Map<String, Object> getVendorAnalytics();
}
