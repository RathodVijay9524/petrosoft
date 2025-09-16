        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.time.LocalDateTime;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class ShiftDTO {
            private Long id;
            private Long pumpId;
            private Long operatorId;
            private LocalDateTime openedAt;
            private LocalDateTime closedAt;
            private String status;
        }
    
