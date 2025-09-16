        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.time.LocalDateTime;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class AuditLogDTO {
            private Long id;
            private String entityName;
            private Long entityId;
            private String action;
            private String performedBy;
            private LocalDateTime performedAt;
            private String details;
        }
    
