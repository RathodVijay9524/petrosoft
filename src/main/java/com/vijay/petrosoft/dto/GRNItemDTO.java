        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.math.BigDecimal;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class GRNItemDTO {
            private Long id;
            private Long purchaseId;
            private Long tankId;
            private BigDecimal quantity;
            private BigDecimal rate;
        }
    
