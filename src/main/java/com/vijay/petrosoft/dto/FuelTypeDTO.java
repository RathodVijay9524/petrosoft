        package com.vijay.petrosoft.dto;
        import lombok.*;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class FuelTypeDTO {
            private Long id;
            private String name;
            private String uom;
        }
    
