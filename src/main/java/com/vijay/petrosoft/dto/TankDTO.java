        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.math.BigDecimal;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class TankDTO {
            private Long id;
            private Long pumpId;
            private Long fuelTypeId;
            private String name;
            private BigDecimal capacity;
            private BigDecimal currentDip;
        }
    
