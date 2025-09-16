        package com.vijay.petrosoft.dto;
        import lombok.*;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class EmployeeDTO {
            private Long id;
            private String employeeCode;
            private String name;
            private String role;
            private String phone;
            private String email;
        }
    
