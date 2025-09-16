package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "master_setup")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false)
public class MasterSetup extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long pumpId;
    
    // Company Information
    @Column(nullable = false, length = 200)
    private String companyName;
    
    @Column(length = 100)
    private String companyCode;
    
    @Column(length = 500)
    private String companyAddress;
    
    @Column(length = 20)
    private String companyPhone;
    
    @Column(length = 100)
    private String companyEmail;
    
    @Column(length = 50)
    private String gstNumber;
    
    @Column(length = 50)
    private String panNumber;
    
    // Financial Year Settings
    @Column(length = 20)
    private String currentFinancialYear;
    
    private LocalDateTime financialYearStart;
    private LocalDateTime financialYearEnd;
    
    // Business Settings
    @Column(length = 20)
    private String currency = "INR";
    
    @Column(length = 10)
    private String timeZone = "Asia/Kolkata";
    
    @Column(length = 20)
    private String dateFormat = "dd/MM/yyyy";
    
    @Column(length = 20)
    private String timeFormat = "HH:mm:ss";
    
    // Notification Settings
    private boolean emailNotificationsEnabled = true;
    private boolean smsNotificationsEnabled = true;
    private String notificationEmail;
    private String notificationPhone;
    
    // System Settings
    private boolean autoBackupEnabled = true;
    private int backupRetentionDays = 30;
    private boolean auditTrailEnabled = true;
    
    // Subscription Settings
    private boolean subscriptionActive = true;
    private LocalDateTime subscriptionExpiry;
    private String subscriptionPlan;
    
    // Support Information
    private String supportPhone = "9561095610";
    private String supportEmail = "support@vritti.co.in";
    
    @Builder.Default
    private boolean active = true;
    
    @Column(length = 1000)
    private String additionalSettings; // JSON for custom settings
}
