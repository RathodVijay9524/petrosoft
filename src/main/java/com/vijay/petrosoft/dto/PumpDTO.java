        package com.vijay.petrosoft.dto;
        import lombok.*;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class PumpDTO {
            private Long id;
            private String name;
            private String code;
            private String address;
            private String city;
            private String state;
            private String gstNumber;
            private String contactPhone;
        }
    
