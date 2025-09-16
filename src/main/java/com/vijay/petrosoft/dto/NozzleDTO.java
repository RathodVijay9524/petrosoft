        package com.vijay.petrosoft.dto;
        import lombok.*;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class NozzleDTO {
            private Long id;
            private Long pumpId;
            private Long fuelTypeId;
            private String name;
            private String dispenserCode;
        }
    
