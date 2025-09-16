        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.math.BigDecimal;
        import java.time.LocalDateTime;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class SaleTransactionDTO {
            private Long id;
            private Long pumpId;
            private Long shiftId;
            private Long nozzleId;
            private Long fuelTypeId;
            private BigDecimal quantity;
            private BigDecimal rate;
            private BigDecimal amount;
            private boolean creditSale;
            private Long customerId;
            private LocalDateTime transactedAt;
        }
    
