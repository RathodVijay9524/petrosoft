package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "grn_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GRNItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long purchaseId;
    private Long tankId;
    private BigDecimal quantity;
    private BigDecimal rate;
}
