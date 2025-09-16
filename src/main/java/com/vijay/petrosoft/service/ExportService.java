package com.vijay.petrosoft.service;

import java.time.LocalDate;
import java.util.Map;

public interface ExportService {
    
    // Sales Data Export
    Map<String, Object> exportSalesData();
    Map<String, Object> exportSalesDataByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Customer Data Export
    Map<String, Object> exportCustomerData();
    Map<String, Object> exportCustomerDataByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Financial Data Export
    Map<String, Object> exportFinancialData();
    Map<String, Object> exportFinancialDataByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Reports Export
    Map<String, Object> exportReports();
    Map<String, Object> exportReportByType(String reportType);
    
    // Employee Data Export
    Map<String, Object> exportEmployeeData();
    
    // Vendor Data Export
    Map<String, Object> exportVendorData();
}
