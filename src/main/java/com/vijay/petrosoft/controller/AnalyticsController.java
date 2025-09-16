package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesAnalytics() {
        Map<String, Object> analytics = analyticsService.getSalesAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/sales/date-range")
    public ResponseEntity<Map<String, Object>> getSalesAnalyticsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> analytics = analyticsService.getSalesAnalyticsByDateRange(startDate, endDate);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomerAnalytics() {
        Map<String, Object> analytics = analyticsService.getCustomerAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/customers/date-range")
    public ResponseEntity<Map<String, Object>> getCustomerAnalyticsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> analytics = analyticsService.getCustomerAnalyticsByDateRange(startDate, endDate);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/financial")
    public ResponseEntity<Map<String, Object>> getFinancialAnalytics() {
        Map<String, Object> analytics = analyticsService.getFinancialAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/financial/date-range")
    public ResponseEntity<Map<String, Object>> getFinancialAnalyticsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> analytics = analyticsService.getFinancialAnalyticsByDateRange(startDate, endDate);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getEmployeeAnalytics() {
        Map<String, Object> analytics = analyticsService.getEmployeeAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/vendors")
    public ResponseEntity<Map<String, Object>> getVendorAnalytics() {
        Map<String, Object> analytics = analyticsService.getVendorAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}
