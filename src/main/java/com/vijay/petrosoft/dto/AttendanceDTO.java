        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.time.LocalDate;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class AttendanceDTO {
            private Long id;
            private Long employeeId;
            private LocalDate date;
            private String status;
        }
    
