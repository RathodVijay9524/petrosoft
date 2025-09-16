package com.vijay.petrosoft.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MasterSetupDTO {
    private Long id;
    
    @NotNull(message = "Pump ID is required")
    private Long pumpId;
    
    // Company Information
    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;
    
    @Size(max = 100, message = "Company code must not exceed 100 characters")
    private String companyCode;
    
    @Size(max = 500, message = "Company address must not exceed 500 characters")
    private String companyAddress;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String companyPhone;
    
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String companyEmail;
    
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "GST number should be valid")
    private String gstNumber;
    
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "PAN number should be valid")
    private String panNumber;
    
    // Financial Year Settings
    @Size(max = 20, message = "Financial year must not exceed 20 characters")
    private String currentFinancialYear;
    
    private LocalDateTime financialYearStart;
    private LocalDateTime financialYearEnd;
    
    // Business Settings
    @Builder.Default
    private String currency = "INR";
    
    @Builder.Default
    private String timeZone = "Asia/Kolkata";
    
    @Builder.Default
    private String dateFormat = "dd/MM/yyyy";
    
    @Builder.Default
    private String timeFormat = "HH:mm:ss";
    
    // Notification Settings
    @Builder.Default
    private boolean emailNotificationsEnabled = true;
    
    @Builder.Default
    private boolean smsNotificationsEnabled = true;
    
    @Email(message = "Notification email should be valid")
    private String notificationEmail;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Notification phone should be valid")
    private String notificationPhone;
    
    // System Settings
    @Builder.Default
    private boolean autoBackupEnabled = true;
    
    @Min(value = 1, message = "Backup retention days must be at least 1")
    @Max(value = 365, message = "Backup retention days must not exceed 365")
    private Integer backupRetentionDays = 30;
    
    @Builder.Default
    private boolean auditTrailEnabled = true;
    
    // Subscription Settings
    @Builder.Default
    private boolean subscriptionActive = true;
    
    private LocalDateTime subscriptionExpiry;
    private String subscriptionPlan;
    
    // Support Information
    @Builder.Default
    private String supportPhone = "9561095610";
    
    @Builder.Default
    private String supportEmail = "support@vritti.co.in";
    
    @Builder.Default
    private boolean active = true;
    
    private Map<String, Object> additionalSettings;
}
