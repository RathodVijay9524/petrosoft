package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false)
public class Notification extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;
    
    @Column(nullable = false)
    private String recipientEmail;
    
    private String recipientPhone;
    
    private Long pumpId;
    
    private Long userId;
    
    private String templateName;
    
    @Column(length = 2000)
    private String templateData; // JSON data for template variables
    
    private LocalDateTime scheduledAt;
    
    private LocalDateTime sentAt;
    
    private String errorMessage;
    
    private Integer retryCount;
    
    @Builder.Default
    private Integer maxRetries = 3;
    
    public enum NotificationType {
        EMAIL, SMS, PUSH, SYSTEM
    }
    
    public enum NotificationStatus {
        PENDING, SENT, FAILED, CANCELLED
    }
}
