package com.vijay.petrosoft.service;

import com.vijay.petrosoft.dto.MasterSetupDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MasterSetupService {
    
    // CRUD Operations
    MasterSetupDTO createMasterSetup(MasterSetupDTO masterSetupDTO);
    MasterSetupDTO updateMasterSetup(Long id, MasterSetupDTO masterSetupDTO);
    Optional<MasterSetupDTO> getMasterSetupById(Long id);
    Optional<MasterSetupDTO> getMasterSetupByPumpId(Long pumpId);
    List<MasterSetupDTO> getAllMasterSetups();
    void deleteMasterSetup(Long id);
    
    // Company Management
    MasterSetupDTO updateCompanyInfo(Long id, MasterSetupDTO companyInfo);
    MasterSetupDTO updateFinancialYear(Long id, String financialYear, LocalDateTime startDate, LocalDateTime endDate);
    MasterSetupDTO updateBusinessSettings(Long id, Map<String, Object> settings);
    
    // Notification Settings
    MasterSetupDTO updateNotificationSettings(Long id, boolean emailEnabled, boolean smsEnabled, 
                                            String notificationEmail, String notificationPhone);
    
    // System Settings
    MasterSetupDTO updateSystemSettings(Long id, boolean autoBackup, int retentionDays, boolean auditTrail);
    
    // Subscription Management
    MasterSetupDTO updateSubscriptionStatus(Long id, boolean active, String plan, LocalDateTime expiry);
    List<MasterSetupDTO> getExpiringSubscriptions(int daysBeforeExpiry);
    List<MasterSetupDTO> getExpiredSubscriptions();
    MasterSetupDTO sendSubscriptionExpiryNotification(Long id);
    
    // Support and Contact
    MasterSetupDTO updateSupportInfo(Long id, String supportPhone, String supportEmail);
    
    // Validation and Checks
    boolean validateGstNumber(String gstNumber);
    boolean validatePanNumber(String panNumber);
    boolean isFinancialYearValid(String financialYear);
    
    // Analytics and Reports
    Map<String, Object> getMasterSetupAnalytics();
    Map<String, Object> getSubscriptionAnalytics();
    List<MasterSetupDTO> searchByCompanyName(String companyName);
    
    // Bulk Operations
    List<MasterSetupDTO> bulkUpdateFinancialYear(List<Long> setupIds, String financialYear);
    void sendBulkSubscriptionExpiryNotifications(List<Long> setupIds);
    
    // Initialization
    MasterSetupDTO createDefaultMasterSetup(Long pumpId, String companyName);
    MasterSetupDTO initializeFromTemplate(Long pumpId, String templateName);
}
