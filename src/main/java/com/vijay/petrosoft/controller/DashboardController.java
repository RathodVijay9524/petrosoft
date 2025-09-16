package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.DashboardDTO;
import com.vijay.petrosoft.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable Long pumpId) {
        DashboardDTO dashboard = dashboardService.getDashboard(pumpId);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @GetMapping("/global")
    public ResponseEntity<DashboardDTO> getGlobalDashboard() {
        DashboardDTO dashboard = dashboardService.getGlobalDashboard();
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/date-range")
    public ResponseEntity<DashboardDTO> getDashboardByDateRange(
            @PathVariable Long pumpId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        DashboardDTO dashboard = dashboardService.getDashboardByDateRange(pumpId, startDate, endDate);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/overview")
    public ResponseEntity<Map<String, Object>> getOverviewMetrics(@PathVariable Long pumpId) {
        Map<String, Object> metrics = dashboardService.getOverviewMetrics(pumpId);
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/overview/date-range")
    public ResponseEntity<Map<String, Object>> getOverviewMetricsForDateRange(
            @PathVariable Long pumpId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> metrics = dashboardService.getOverviewMetricsForDateRange(pumpId, startDate, endDate);
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/sales")
    public ResponseEntity<Map<String, Object>> getSalesAnalytics(@PathVariable Long pumpId) {
        Map<String, Object> analytics = dashboardService.getSalesAnalytics(pumpId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/sales/date-range")
    public ResponseEntity<Map<String, Object>> getSalesAnalyticsByDateRange(
            @PathVariable Long pumpId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> analytics = dashboardService.getSalesAnalyticsByDateRange(pumpId, startDate, endDate);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/sales/chart/{period}")
    public ResponseEntity<List<DashboardDTO.SalesChartDTO>> getSalesChartData(
            @PathVariable Long pumpId, @PathVariable String period) {
        List<DashboardDTO.SalesChartDTO> chartData = dashboardService.getSalesChartData(pumpId, period);
        return new ResponseEntity<>(chartData, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics(@PathVariable Long pumpId) {
        Map<String, Object> analytics = dashboardService.getRevenueAnalytics(pumpId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/revenue/chart/{period}")
    public ResponseEntity<List<DashboardDTO.RevenueChartDTO>> getRevenueChartData(
            @PathVariable Long pumpId, @PathVariable String period) {
        List<DashboardDTO.RevenueChartDTO> chartData = dashboardService.getRevenueChartData(pumpId, period);
        return new ResponseEntity<>(chartData, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/customers")
    public ResponseEntity<Map<String, Object>> getCustomerAnalytics(@PathVariable Long pumpId) {
        Map<String, Object> analytics = dashboardService.getCustomerAnalytics(pumpId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/customers/chart/{period}")
    public ResponseEntity<List<DashboardDTO.CustomerChartDTO>> getCustomerChartData(
            @PathVariable Long pumpId, @PathVariable String period) {
        List<DashboardDTO.CustomerChartDTO> chartData = dashboardService.getCustomerChartData(pumpId, period);
        return new ResponseEntity<>(chartData, HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<Map<String, Object>> getSubscriptionAnalytics() {
        Map<String, Object> analytics = dashboardService.getSubscriptionAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/subscriptions/date-range")
    public ResponseEntity<Map<String, Object>> getSubscriptionAnalyticsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> analytics = dashboardService.getSubscriptionAnalyticsByDateRange(startDate, endDate);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/top-pumps/{limit}")
    public ResponseEntity<List<DashboardDTO.TopPumpDTO>> getTopPumpsBySales(@PathVariable Long limit) {
        List<DashboardDTO.TopPumpDTO> topPumps = dashboardService.getTopPumpsBySales(limit);
        return new ResponseEntity<>(topPumps, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/top-customers/{limit}")
    public ResponseEntity<List<DashboardDTO.TopCustomerDTO>> getTopCustomersByPurchase(
            @PathVariable Long pumpId, @PathVariable Long limit) {
        List<DashboardDTO.TopCustomerDTO> topCustomers = dashboardService.getTopCustomersByPurchase(pumpId, limit);
        return new ResponseEntity<>(topCustomers, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/top-fuel-types/{limit}")
    public ResponseEntity<List<DashboardDTO.TopFuelTypeDTO>> getTopFuelTypes(
            @PathVariable Long pumpId, @PathVariable Long limit) {
        List<DashboardDTO.TopFuelTypeDTO> topFuelTypes = dashboardService.getTopFuelTypes(pumpId, limit);
        return new ResponseEntity<>(topFuelTypes, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/recent-sales/{limit}")
    public ResponseEntity<List<DashboardDTO.RecentSaleDTO>> getRecentSales(
            @PathVariable Long pumpId, @PathVariable Long limit) {
        List<DashboardDTO.RecentSaleDTO> recentSales = dashboardService.getRecentSales(pumpId, limit);
        return new ResponseEntity<>(recentSales, HttpStatus.OK);
    }

    @GetMapping("/recent-payments/{limit}")
    public ResponseEntity<List<DashboardDTO.RecentPaymentDTO>> getRecentPayments(@PathVariable Long limit) {
        List<DashboardDTO.RecentPaymentDTO> recentPayments = dashboardService.getRecentPayments(limit);
        return new ResponseEntity<>(recentPayments, HttpStatus.OK);
    }

    @GetMapping("/recent-subscriptions/{limit}")
    public ResponseEntity<List<DashboardDTO.RecentSubscriptionDTO>> getRecentSubscriptions(@PathVariable Long limit) {
        List<DashboardDTO.RecentSubscriptionDTO> recentSubscriptions = dashboardService.getRecentSubscriptions(limit);
        return new ResponseEntity<>(recentSubscriptions, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/alerts")
    public ResponseEntity<List<DashboardDTO.AlertDTO>> getAlerts(@PathVariable Long pumpId) {
        List<DashboardDTO.AlertDTO> alerts = dashboardService.getAlerts(pumpId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/alerts/system")
    public ResponseEntity<List<DashboardDTO.AlertDTO>> getSystemAlerts() {
        List<DashboardDTO.AlertDTO> alerts = dashboardService.getSystemAlerts();
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/realtime")
    public ResponseEntity<Map<String, Object>> getRealTimeMetrics(@PathVariable Long pumpId) {
        Map<String, Object> metrics = dashboardService.getRealTimeMetrics(pumpId);
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/realtime/sales")
    public ResponseEntity<Map<String, Object>> getRealTimeSales(@PathVariable Long pumpId) {
        Map<String, Object> sales = dashboardService.getRealTimeSales(pumpId);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/compare-periods")
    public ResponseEntity<Map<String, Object>> comparePeriods(
            @PathVariable Long pumpId,
            @RequestParam LocalDate period1Start,
            @RequestParam LocalDate period1End,
            @RequestParam LocalDate period2Start,
            @RequestParam LocalDate period2End) {
        Map<String, Object> comparison = dashboardService.comparePeriods(
                pumpId, period1Start, period1End, period2Start, period2End);
        return new ResponseEntity<>(comparison, HttpStatus.OK);
    }

    @GetMapping("/compare-pumps")
    public ResponseEntity<Map<String, Object>> comparePumps(
            @RequestParam List<Long> pumpIds,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> comparison = dashboardService.comparePumps(pumpIds, startDate, endDate);
        return new ResponseEntity<>(comparison, HttpStatus.OK);
    }

    // Additional endpoints for comprehensive testing
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> overview = dashboardService.getOverview();
        return new ResponseEntity<>(overview, HttpStatus.OK);
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = dashboardService.getMetrics();
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs() {
        Map<String, Object> kpis = dashboardService.getKPIs();
        return new ResponseEntity<>(kpis, HttpStatus.OK);
    }
}
