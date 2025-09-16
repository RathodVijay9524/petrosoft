package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.MasterSetup;
import com.vijay.petrosoft.dto.MasterSetupDTO;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.vijay.petrosoft.exception.GlobalExceptionHandler.DuplicateResourceException;
import com.vijay.petrosoft.repository.MasterSetupRepository;
import com.vijay.petrosoft.service.MasterSetupService;
import com.vijay.petrosoft.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MasterSetupServiceImpl implements MasterSetupService {

    private final MasterSetupRepository masterSetupRepository;
    private final NotificationService notificationService;

    // GST Number Pattern: 22ABCDE1234F1Z5
    private static final Pattern GST_PATTERN = Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$");
    
    // PAN Number Pattern: ABCDE1234F
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");

    @Override
    public MasterSetupDTO createMasterSetup(MasterSetupDTO masterSetupDTO) {
        // Check if master setup already exists for this pump
        if (masterSetupRepository.existsByPumpId(masterSetupDTO.getPumpId())) {
            throw new DuplicateResourceException("Master setup already exists for pump ID: " + masterSetupDTO.getPumpId());
        }

        // Validate GST and PAN if provided
        if (masterSetupDTO.getGstNumber() != null && !validateGstNumber(masterSetupDTO.getGstNumber())) {
            throw new IllegalArgumentException("Invalid GST number format");
        }

        if (masterSetupDTO.getPanNumber() != null && !validatePanNumber(masterSetupDTO.getPanNumber())) {
            throw new IllegalArgumentException("Invalid PAN number format");
        }

        MasterSetup masterSetup = MasterSetup.builder()
                .pumpId(masterSetupDTO.getPumpId())
                .companyName(masterSetupDTO.getCompanyName())
                .companyCode(masterSetupDTO.getCompanyCode())
                .companyAddress(masterSetupDTO.getCompanyAddress())
                .companyPhone(masterSetupDTO.getCompanyPhone())
                .companyEmail(masterSetupDTO.getCompanyEmail())
                .gstNumber(masterSetupDTO.getGstNumber())
                .panNumber(masterSetupDTO.getPanNumber())
                .currentFinancialYear(masterSetupDTO.getCurrentFinancialYear())
                .financialYearStart(masterSetupDTO.getFinancialYearStart())
                .financialYearEnd(masterSetupDTO.getFinancialYearEnd())
                .currency(masterSetupDTO.getCurrency())
                .timeZone(masterSetupDTO.getTimeZone())
                .dateFormat(masterSetupDTO.getDateFormat())
                .timeFormat(masterSetupDTO.getTimeFormat())
                .emailNotificationsEnabled(masterSetupDTO.isEmailNotificationsEnabled())
                .smsNotificationsEnabled(masterSetupDTO.isSmsNotificationsEnabled())
                .notificationEmail(masterSetupDTO.getNotificationEmail())
                .notificationPhone(masterSetupDTO.getNotificationPhone())
                .autoBackupEnabled(masterSetupDTO.isAutoBackupEnabled())
                .backupRetentionDays(masterSetupDTO.getBackupRetentionDays())
                .auditTrailEnabled(masterSetupDTO.isAuditTrailEnabled())
                .subscriptionActive(masterSetupDTO.isSubscriptionActive())
                .subscriptionExpiry(masterSetupDTO.getSubscriptionExpiry())
                .subscriptionPlan(masterSetupDTO.getSubscriptionPlan())
                .supportPhone(masterSetupDTO.getSupportPhone())
                .supportEmail(masterSetupDTO.getSupportEmail())
                .active(masterSetupDTO.isActive())
                .additionalSettings(masterSetupDTO.getAdditionalSettings() != null ? 
                    masterSetupDTO.getAdditionalSettings().toString() : null)
                .build();

        MasterSetup savedMasterSetup = masterSetupRepository.save(masterSetup);
        log.info("Master setup created for pump ID: {}", masterSetupDTO.getPumpId());
        
        return convertToDTO(savedMasterSetup);
    }

    @Override
    public MasterSetupDTO updateMasterSetup(Long id, MasterSetupDTO masterSetupDTO) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        // Validate GST and PAN if provided
        if (masterSetupDTO.getGstNumber() != null && !validateGstNumber(masterSetupDTO.getGstNumber())) {
            throw new IllegalArgumentException("Invalid GST number format");
        }

        if (masterSetupDTO.getPanNumber() != null && !validatePanNumber(masterSetupDTO.getPanNumber())) {
            throw new IllegalArgumentException("Invalid PAN number format");
        }

        // Update all fields
        masterSetup.setCompanyName(masterSetupDTO.getCompanyName());
        masterSetup.setCompanyCode(masterSetupDTO.getCompanyCode());
        masterSetup.setCompanyAddress(masterSetupDTO.getCompanyAddress());
        masterSetup.setCompanyPhone(masterSetupDTO.getCompanyPhone());
        masterSetup.setCompanyEmail(masterSetupDTO.getCompanyEmail());
        masterSetup.setGstNumber(masterSetupDTO.getGstNumber());
        masterSetup.setPanNumber(masterSetupDTO.getPanNumber());
        masterSetup.setCurrentFinancialYear(masterSetupDTO.getCurrentFinancialYear());
        masterSetup.setFinancialYearStart(masterSetupDTO.getFinancialYearStart());
        masterSetup.setFinancialYearEnd(masterSetupDTO.getFinancialYearEnd());
        masterSetup.setCurrency(masterSetupDTO.getCurrency());
        masterSetup.setTimeZone(masterSetupDTO.getTimeZone());
        masterSetup.setDateFormat(masterSetupDTO.getDateFormat());
        masterSetup.setTimeFormat(masterSetupDTO.getTimeFormat());
        masterSetup.setEmailNotificationsEnabled(masterSetupDTO.isEmailNotificationsEnabled());
        masterSetup.setSmsNotificationsEnabled(masterSetupDTO.isSmsNotificationsEnabled());
        masterSetup.setNotificationEmail(masterSetupDTO.getNotificationEmail());
        masterSetup.setNotificationPhone(masterSetupDTO.getNotificationPhone());
        masterSetup.setAutoBackupEnabled(masterSetupDTO.isAutoBackupEnabled());
        masterSetup.setBackupRetentionDays(masterSetupDTO.getBackupRetentionDays());
        masterSetup.setAuditTrailEnabled(masterSetupDTO.isAuditTrailEnabled());
        masterSetup.setSubscriptionActive(masterSetupDTO.isSubscriptionActive());
        masterSetup.setSubscriptionExpiry(masterSetupDTO.getSubscriptionExpiry());
        masterSetup.setSubscriptionPlan(masterSetupDTO.getSubscriptionPlan());
        masterSetup.setSupportPhone(masterSetupDTO.getSupportPhone());
        masterSetup.setSupportEmail(masterSetupDTO.getSupportEmail());
        masterSetup.setActive(masterSetupDTO.isActive());
        masterSetup.setAdditionalSettings(masterSetupDTO.getAdditionalSettings() != null ? 
            masterSetupDTO.getAdditionalSettings().toString() : null);

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        log.info("Master setup updated for ID: {}", id);
        
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MasterSetupDTO> getMasterSetupById(Long id) {
        return masterSetupRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MasterSetupDTO> getMasterSetupByPumpId(Long pumpId) {
        return masterSetupRepository.findByPumpId(pumpId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterSetupDTO> getAllMasterSetups() {
        return masterSetupRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMasterSetup(Long id) {
        if (!masterSetupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Master setup not found with id: " + id);
        }
        masterSetupRepository.deleteById(id);
        log.info("Master setup deleted for ID: {}", id);
    }

    @Override
    public MasterSetupDTO updateCompanyInfo(Long id, MasterSetupDTO companyInfo) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        masterSetup.setCompanyName(companyInfo.getCompanyName());
        masterSetup.setCompanyCode(companyInfo.getCompanyCode());
        masterSetup.setCompanyAddress(companyInfo.getCompanyAddress());
        masterSetup.setCompanyPhone(companyInfo.getCompanyPhone());
        masterSetup.setCompanyEmail(companyInfo.getCompanyEmail());
        masterSetup.setGstNumber(companyInfo.getGstNumber());
        masterSetup.setPanNumber(companyInfo.getPanNumber());

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    public MasterSetupDTO updateFinancialYear(Long id, String financialYear, LocalDateTime startDate, LocalDateTime endDate) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        if (!isFinancialYearValid(financialYear)) {
            throw new IllegalArgumentException("Invalid financial year format");
        }

        masterSetup.setCurrentFinancialYear(financialYear);
        masterSetup.setFinancialYearStart(startDate);
        masterSetup.setFinancialYearEnd(endDate);

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        log.info("Financial year updated to {} for master setup ID: {}", financialYear, id);
        
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    public MasterSetupDTO updateBusinessSettings(Long id, Map<String, Object> settings) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        if (settings.containsKey("currency")) {
            masterSetup.setCurrency(settings.get("currency").toString());
        }
        if (settings.containsKey("timeZone")) {
            masterSetup.setTimeZone(settings.get("timeZone").toString());
        }
        if (settings.containsKey("dateFormat")) {
            masterSetup.setDateFormat(settings.get("dateFormat").toString());
        }
        if (settings.containsKey("timeFormat")) {
            masterSetup.setTimeFormat(settings.get("timeFormat").toString());
        }

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    public MasterSetupDTO updateNotificationSettings(Long id, boolean emailEnabled, boolean smsEnabled, 
                                                   String notificationEmail, String notificationPhone) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        masterSetup.setEmailNotificationsEnabled(emailEnabled);
        masterSetup.setSmsNotificationsEnabled(smsEnabled);
        masterSetup.setNotificationEmail(notificationEmail);
        masterSetup.setNotificationPhone(notificationPhone);

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    public MasterSetupDTO updateSystemSettings(Long id, boolean autoBackup, int retentionDays, boolean auditTrail) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        masterSetup.setAutoBackupEnabled(autoBackup);
        masterSetup.setBackupRetentionDays(retentionDays);
        masterSetup.setAuditTrailEnabled(auditTrail);

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    public MasterSetupDTO updateSubscriptionStatus(Long id, boolean active, String plan, LocalDateTime expiry) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        masterSetup.setSubscriptionActive(active);
        masterSetup.setSubscriptionPlan(plan);
        masterSetup.setSubscriptionExpiry(expiry);

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        
        // Send notification if subscription is expiring
        if (!active || (expiry != null && expiry.isBefore(LocalDateTime.now().plusDays(7)))) {
            sendSubscriptionExpiryNotification(id);
        }
        
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterSetupDTO> getExpiringSubscriptions(int daysBeforeExpiry) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(daysBeforeExpiry);
        return masterSetupRepository.findExpiringSubscriptions(expiryDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterSetupDTO> getExpiredSubscriptions() {
        return masterSetupRepository.findExpiredSubscriptions(LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MasterSetupDTO sendSubscriptionExpiryNotification(Long id) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        String message = String.format(
            "Your subscription for %s plan is expiring on %s. Please renew to continue using our services.",
            masterSetup.getSubscriptionPlan(),
            masterSetup.getSubscriptionExpiry() != null ? 
                masterSetup.getSubscriptionExpiry().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"
        );

        // Send email notification
        if (masterSetup.isEmailNotificationsEnabled() && masterSetup.getNotificationEmail() != null) {
            notificationService.sendEmailNotification(
                masterSetup.getNotificationEmail(),
                "Subscription Expiry Alert - Petrosoft",
                message
            );
        }

        // Send SMS notification
        if (masterSetup.isSmsNotificationsEnabled() && masterSetup.getNotificationPhone() != null) {
            notificationService.sendSMSNotification(
                masterSetup.getNotificationPhone(),
                message
            );
        }

        log.info("Subscription expiry notification sent for master setup ID: {}", id);
        return convertToDTO(masterSetup);
    }

    @Override
    public MasterSetupDTO updateSupportInfo(Long id, String supportPhone, String supportEmail) {
        MasterSetup masterSetup = masterSetupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master setup not found with id: " + id));

        masterSetup.setSupportPhone(supportPhone);
        masterSetup.setSupportEmail(supportEmail);

        MasterSetup updatedMasterSetup = masterSetupRepository.save(masterSetup);
        return convertToDTO(updatedMasterSetup);
    }

    @Override
    public boolean validateGstNumber(String gstNumber) {
        return GST_PATTERN.matcher(gstNumber).matches();
    }

    @Override
    public boolean validatePanNumber(String panNumber) {
        return PAN_PATTERN.matcher(panNumber).matches();
    }

    @Override
    public boolean isFinancialYearValid(String financialYear) {
        // Simple validation for financial year format (e.g., "2024-25")
        return financialYear != null && financialYear.matches("^\\d{4}-\\d{2}$");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMasterSetupAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalSetups", masterSetupRepository.count());
        analytics.put("activeSetups", masterSetupRepository.countBySubscriptionActiveTrue());
        analytics.put("expiringSubscriptions", masterSetupRepository.countExpiringSubscriptions(LocalDateTime.now().plusDays(7)));
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSubscriptionAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("activeSubscriptions", masterSetupRepository.countBySubscriptionActiveTrue());
        analytics.put("expiringSubscriptions", masterSetupRepository.countExpiringSubscriptions(LocalDateTime.now().plusDays(7)));
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterSetupDTO> searchByCompanyName(String companyName) {
        return masterSetupRepository.findByCompanyNameContaining(companyName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MasterSetupDTO> bulkUpdateFinancialYear(List<Long> setupIds, String financialYear) {
        List<MasterSetupDTO> updatedSetups = new ArrayList<>();
        
        for (Long id : setupIds) {
            try {
                MasterSetupDTO updatedSetup = updateFinancialYear(id, financialYear, 
                    LocalDateTime.now(), LocalDateTime.now().plusYears(1));
                updatedSetups.add(updatedSetup);
            } catch (Exception e) {
                log.error("Failed to update financial year for setup ID: {}", id, e);
            }
        }
        
        return updatedSetups;
    }

    @Override
    public void sendBulkSubscriptionExpiryNotifications(List<Long> setupIds) {
        for (Long id : setupIds) {
            try {
                sendSubscriptionExpiryNotification(id);
            } catch (Exception e) {
                log.error("Failed to send subscription expiry notification for setup ID: {}", id, e);
            }
        }
    }

    @Override
    public MasterSetupDTO createDefaultMasterSetup(Long pumpId, String companyName) {
        MasterSetupDTO defaultSetup = MasterSetupDTO.builder()
                .pumpId(pumpId)
                .companyName(companyName)
                .currentFinancialYear(generateCurrentFinancialYear())
                .financialYearStart(LocalDateTime.now())
                .financialYearEnd(LocalDateTime.now().plusYears(1))
                .currency("INR")
                .timeZone("Asia/Kolkata")
                .dateFormat("dd/MM/yyyy")
                .timeFormat("HH:mm:ss")
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(true)
                .autoBackupEnabled(true)
                .backupRetentionDays(30)
                .auditTrailEnabled(true)
                .subscriptionActive(true)
                .subscriptionPlan("Basic")
                .subscriptionExpiry(LocalDateTime.now().plusMonths(1))
                .supportPhone("9561095610")
                .supportEmail("support@vritti.co.in")
                .active(true)
                .build();

        return createMasterSetup(defaultSetup);
    }

    @Override
    public MasterSetupDTO initializeFromTemplate(Long pumpId, String templateName) {
        // This would typically load from a template configuration
        // For now, create a default setup
        return createDefaultMasterSetup(pumpId, "Company Name");
    }

    private String generateCurrentFinancialYear() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        
        // Indian financial year starts from April
        if (now.getMonthValue() >= 4) {
            return currentYear + "-" + String.format("%02d", (currentYear + 1) % 100);
        } else {
            return (currentYear - 1) + "-" + String.format("%02d", currentYear % 100);
        }
    }

    private MasterSetupDTO convertToDTO(MasterSetup masterSetup) {
        return MasterSetupDTO.builder()
                .id(masterSetup.getId())
                .pumpId(masterSetup.getPumpId())
                .companyName(masterSetup.getCompanyName())
                .companyCode(masterSetup.getCompanyCode())
                .companyAddress(masterSetup.getCompanyAddress())
                .companyPhone(masterSetup.getCompanyPhone())
                .companyEmail(masterSetup.getCompanyEmail())
                .gstNumber(masterSetup.getGstNumber())
                .panNumber(masterSetup.getPanNumber())
                .currentFinancialYear(masterSetup.getCurrentFinancialYear())
                .financialYearStart(masterSetup.getFinancialYearStart())
                .financialYearEnd(masterSetup.getFinancialYearEnd())
                .currency(masterSetup.getCurrency())
                .timeZone(masterSetup.getTimeZone())
                .dateFormat(masterSetup.getDateFormat())
                .timeFormat(masterSetup.getTimeFormat())
                .emailNotificationsEnabled(masterSetup.isEmailNotificationsEnabled())
                .smsNotificationsEnabled(masterSetup.isSmsNotificationsEnabled())
                .notificationEmail(masterSetup.getNotificationEmail())
                .notificationPhone(masterSetup.getNotificationPhone())
                .autoBackupEnabled(masterSetup.isAutoBackupEnabled())
                .backupRetentionDays(masterSetup.getBackupRetentionDays())
                .auditTrailEnabled(masterSetup.isAuditTrailEnabled())
                .subscriptionActive(masterSetup.isSubscriptionActive())
                .subscriptionExpiry(masterSetup.getSubscriptionExpiry())
                .subscriptionPlan(masterSetup.getSubscriptionPlan())
                .supportPhone(masterSetup.getSupportPhone())
                .supportEmail(masterSetup.getSupportEmail())
                .active(masterSetup.isActive())
                .build();
    }
}
