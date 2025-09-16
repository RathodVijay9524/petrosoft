        package com.vijay.petrosoft.dto;
        import lombok.*;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class VendorDTO {
            private Long id;
            private String name;
            private String phone;
            private String email;
            private String address;
        }
    
