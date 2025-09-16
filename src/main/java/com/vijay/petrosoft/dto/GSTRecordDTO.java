        package com.vijay.petrosoft.dto;
        import lombok.*;
        import java.math.BigDecimal;
        import java.time.LocalDate;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class GSTRecordDTO {
            private Long id;
            private Long pumpId;
            private LocalDate periodStart;
            private LocalDate periodEnd;
            private BigDecimal taxableValue;
            private BigDecimal cgst;
            private BigDecimal sgst;
            private BigDecimal igst;
        }
    
