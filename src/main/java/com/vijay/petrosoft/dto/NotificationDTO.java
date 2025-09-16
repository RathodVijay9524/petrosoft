package com.vijay.petrosoft.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;
    
    @NotNull(message = "Notification type is required")
    private String type; // EMAIL, SMS, PUSH, SYSTEM
    
    private String status; // PENDING, SENT, FAILED, CANCELLED
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Email should be valid")
    private String recipientEmail;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String recipientPhone;
    
    private Long pumpId;
    private Long userId;
    private String templateName;
    private Map<String, Object> templateData;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetries;
}
