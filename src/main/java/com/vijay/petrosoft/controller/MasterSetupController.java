package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.MasterSetupDTO;
import com.vijay.petrosoft.service.MasterSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/master-setup")
@RequiredArgsConstructor
public class MasterSetupController {

    private final MasterSetupService masterSetupService;

    @PostMapping
    public ResponseEntity<MasterSetupDTO> createMasterSetup(@Valid @RequestBody MasterSetupDTO masterSetupDTO) {
        MasterSetupDTO createdSetup = masterSetupService.createMasterSetup(masterSetupDTO);
        return new ResponseEntity<>(createdSetup, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterSetupDTO> getMasterSetupById(@PathVariable Long id) {
        return masterSetupService.getMasterSetupById(id)
                .map(setup -> new ResponseEntity<>(setup, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<MasterSetupDTO> getMasterSetupByPumpId(@PathVariable Long pumpId) {
        return masterSetupService.getMasterSetupByPumpId(pumpId)
                .map(setup -> new ResponseEntity<>(setup, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<MasterSetupDTO>> getAllMasterSetups() {
        List<MasterSetupDTO> setups = masterSetupService.getAllMasterSetups();
        return new ResponseEntity<>(setups, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasterSetupDTO> updateMasterSetup(@PathVariable Long id, @Valid @RequestBody MasterSetupDTO masterSetupDTO) {
        MasterSetupDTO updatedSetup = masterSetupService.updateMasterSetup(id, masterSetupDTO);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @PutMapping("/{id}/company-info")
    public ResponseEntity<MasterSetupDTO> updateCompanyInfo(@PathVariable Long id, @RequestBody MasterSetupDTO companyInfo) {
        MasterSetupDTO updatedSetup = masterSetupService.updateCompanyInfo(id, companyInfo);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @PutMapping("/{id}/financial-year")
    public ResponseEntity<MasterSetupDTO> updateFinancialYear(
            @PathVariable Long id,
            @RequestParam String financialYear,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        MasterSetupDTO updatedSetup = masterSetupService.updateFinancialYear(id, financialYear, startDate, endDate);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @PutMapping("/{id}/business-settings")
    public ResponseEntity<MasterSetupDTO> updateBusinessSettings(@PathVariable Long id, @RequestBody Map<String, Object> settings) {
        MasterSetupDTO updatedSetup = masterSetupService.updateBusinessSettings(id, settings);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @PutMapping("/{id}/notification-settings")
    public ResponseEntity<MasterSetupDTO> updateNotificationSettings(
            @PathVariable Long id,
            @RequestParam boolean emailEnabled,
            @RequestParam boolean smsEnabled,
            @RequestParam(required = false) String notificationEmail,
            @RequestParam(required = false) String notificationPhone) {
        MasterSetupDTO updatedSetup = masterSetupService.updateNotificationSettings(id, emailEnabled, smsEnabled, notificationEmail, notificationPhone);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @PutMapping("/{id}/system-settings")
    public ResponseEntity<MasterSetupDTO> updateSystemSettings(
            @PathVariable Long id,
            @RequestParam boolean autoBackup,
            @RequestParam int retentionDays,
            @RequestParam boolean auditTrail) {
        MasterSetupDTO updatedSetup = masterSetupService.updateSystemSettings(id, autoBackup, retentionDays, auditTrail);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @PutMapping("/{id}/subscription")
    public ResponseEntity<MasterSetupDTO> updateSubscriptionStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            @RequestParam(required = false) String plan,
            @RequestParam(required = false) LocalDateTime expiry) {
        MasterSetupDTO updatedSetup = masterSetupService.updateSubscriptionStatus(id, active, plan, expiry);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @PutMapping("/{id}/support-info")
    public ResponseEntity<MasterSetupDTO> updateSupportInfo(
            @PathVariable Long id,
            @RequestParam String supportPhone,
            @RequestParam String supportEmail) {
        MasterSetupDTO updatedSetup = masterSetupService.updateSupportInfo(id, supportPhone, supportEmail);
        return new ResponseEntity<>(updatedSetup, HttpStatus.OK);
    }

    @GetMapping("/expiring-subscriptions")
    public ResponseEntity<List<MasterSetupDTO>> getExpiringSubscriptions(@RequestParam(defaultValue = "7") int daysBeforeExpiry) {
        List<MasterSetupDTO> setups = masterSetupService.getExpiringSubscriptions(daysBeforeExpiry);
        return new ResponseEntity<>(setups, HttpStatus.OK);
    }

    @GetMapping("/expired-subscriptions")
    public ResponseEntity<List<MasterSetupDTO>> getExpiredSubscriptions() {
        List<MasterSetupDTO> setups = masterSetupService.getExpiredSubscriptions();
        return new ResponseEntity<>(setups, HttpStatus.OK);
    }

    @PostMapping("/{id}/send-expiry-notification")
    public ResponseEntity<MasterSetupDTO> sendSubscriptionExpiryNotification(@PathVariable Long id) {
        MasterSetupDTO setup = masterSetupService.sendSubscriptionExpiryNotification(id);
        return new ResponseEntity<>(setup, HttpStatus.OK);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getMasterSetupAnalytics() {
        Map<String, Object> analytics = masterSetupService.getMasterSetupAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/analytics/subscriptions")
    public ResponseEntity<Map<String, Object>> getSubscriptionAnalytics() {
        Map<String, Object> analytics = masterSetupService.getSubscriptionAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MasterSetupDTO>> searchByCompanyName(@RequestParam String companyName) {
        List<MasterSetupDTO> setups = masterSetupService.searchByCompanyName(companyName);
        return new ResponseEntity<>(setups, HttpStatus.OK);
    }

    @PostMapping("/bulk-update-financial-year")
    public ResponseEntity<List<MasterSetupDTO>> bulkUpdateFinancialYear(
            @RequestBody List<Long> setupIds,
            @RequestParam String financialYear) {
        List<MasterSetupDTO> updatedSetups = masterSetupService.bulkUpdateFinancialYear(setupIds, financialYear);
        return new ResponseEntity<>(updatedSetups, HttpStatus.OK);
    }

    @PostMapping("/bulk-send-expiry-notifications")
    public ResponseEntity<Void> sendBulkSubscriptionExpiryNotifications(@RequestBody List<Long> setupIds) {
        masterSetupService.sendBulkSubscriptionExpiryNotifications(setupIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create-default")
    public ResponseEntity<MasterSetupDTO> createDefaultMasterSetup(
            @RequestParam Long pumpId,
            @RequestParam String companyName) {
        MasterSetupDTO setup = masterSetupService.createDefaultMasterSetup(pumpId, companyName);
        return new ResponseEntity<>(setup, HttpStatus.CREATED);
    }

    @PostMapping("/initialize-from-template")
    public ResponseEntity<MasterSetupDTO> initializeFromTemplate(
            @RequestParam Long pumpId,
            @RequestParam String templateName) {
        MasterSetupDTO setup = masterSetupService.initializeFromTemplate(pumpId, templateName);
        return new ResponseEntity<>(setup, HttpStatus.CREATED);
    }

    @GetMapping("/validate/gst/{gstNumber}")
    public ResponseEntity<Boolean> validateGstNumber(@PathVariable String gstNumber) {
        boolean isValid = masterSetupService.validateGstNumber(gstNumber);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }

    @GetMapping("/validate/pan/{panNumber}")
    public ResponseEntity<Boolean> validatePanNumber(@PathVariable String panNumber) {
        boolean isValid = masterSetupService.validatePanNumber(panNumber);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }

    @GetMapping("/validate/financial-year/{financialYear}")
    public ResponseEntity<Boolean> validateFinancialYear(@PathVariable String financialYear) {
        boolean isValid = masterSetupService.isFinancialYearValid(financialYear);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMasterSetup(@PathVariable Long id) {
        masterSetupService.deleteMasterSetup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
