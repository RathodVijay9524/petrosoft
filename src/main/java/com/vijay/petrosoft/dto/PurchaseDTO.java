        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.math.BigDecimal;
        import java.time.LocalDate;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class PurchaseDTO {
            private Long id;
            private Long pumpId;
            private Long vendorId;
            private LocalDate invoiceDate;
            private String invoiceNumber;
            private BigDecimal totalAmount;
        }
    
