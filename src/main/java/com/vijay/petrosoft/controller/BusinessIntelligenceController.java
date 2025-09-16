package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.service.BusinessIntelligenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/bi")
@RequiredArgsConstructor
public class BusinessIntelligenceController {

    private final BusinessIntelligenceService businessIntelligenceService;

    @GetMapping("/revenue-trends")
    public ResponseEntity<Map<String, Object>> getRevenueTrends() {
        Map<String, Object> trends = businessIntelligenceService.getRevenueTrends();
        return new ResponseEntity<>(trends, HttpStatus.OK);
    }

    @GetMapping("/revenue-trends/date-range")
    public ResponseEntity<Map<String, Object>> getRevenueTrendsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> trends = businessIntelligenceService.getRevenueTrendsByDateRange(startDate, endDate);
        return new ResponseEntity<>(trends, HttpStatus.OK);
    }

    @GetMapping("/customer-insights")
    public ResponseEntity<Map<String, Object>> getCustomerInsights() {
        Map<String, Object> insights = businessIntelligenceService.getCustomerInsights();
        return new ResponseEntity<>(insights, HttpStatus.OK);
    }

    @GetMapping("/customer-insights/date-range")
    public ResponseEntity<Map<String, Object>> getCustomerInsightsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> insights = businessIntelligenceService.getCustomerInsightsByDateRange(startDate, endDate);
        return new ResponseEntity<>(insights, HttpStatus.OK);
    }

    @GetMapping("/performance-metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        Map<String, Object> metrics = businessIntelligenceService.getPerformanceMetrics();
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    @GetMapping("/performance-metrics/date-range")
    public ResponseEntity<Map<String, Object>> getPerformanceMetricsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> metrics = businessIntelligenceService.getPerformanceMetricsByDateRange(startDate, endDate);
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    @GetMapping("/operational-efficiency")
    public ResponseEntity<Map<String, Object>> getOperationalEfficiency() {
        Map<String, Object> efficiency = businessIntelligenceService.getOperationalEfficiency();
        return new ResponseEntity<>(efficiency, HttpStatus.OK);
    }

    @GetMapping("/operational-efficiency/date-range")
    public ResponseEntity<Map<String, Object>> getOperationalEfficiencyByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> efficiency = businessIntelligenceService.getOperationalEfficiencyByDateRange(startDate, endDate);
        return new ResponseEntity<>(efficiency, HttpStatus.OK);
    }
}
