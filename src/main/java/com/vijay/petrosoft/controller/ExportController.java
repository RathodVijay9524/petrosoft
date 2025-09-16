package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> exportSalesData() {
        Map<String, Object> exportData = exportService.exportSalesData();
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/sales/date-range")
    public ResponseEntity<Map<String, Object>> exportSalesDataByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> exportData = exportService.exportSalesDataByDateRange(startDate, endDate);
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> exportCustomerData() {
        Map<String, Object> exportData = exportService.exportCustomerData();
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/customers/date-range")
    public ResponseEntity<Map<String, Object>> exportCustomerDataByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> exportData = exportService.exportCustomerDataByDateRange(startDate, endDate);
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/financial")
    public ResponseEntity<Map<String, Object>> exportFinancialData() {
        Map<String, Object> exportData = exportService.exportFinancialData();
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/financial/date-range")
    public ResponseEntity<Map<String, Object>> exportFinancialDataByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> exportData = exportService.exportFinancialDataByDateRange(startDate, endDate);
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> exportReports() {
        Map<String, Object> exportData = exportService.exportReports();
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/reports/{reportType}")
    public ResponseEntity<Map<String, Object>> exportReportByType(@PathVariable String reportType) {
        Map<String, Object> exportData = exportService.exportReportByType(reportType);
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> exportEmployeeData() {
        Map<String, Object> exportData = exportService.exportEmployeeData();
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }

    @GetMapping("/vendors")
    public ResponseEntity<Map<String, Object>> exportVendorData() {
        Map<String, Object> exportData = exportService.exportVendorData();
        return new ResponseEntity<>(exportData, HttpStatus.OK);
    }
}
