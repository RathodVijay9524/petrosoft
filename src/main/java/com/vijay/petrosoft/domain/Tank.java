package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tanks")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Tank extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pumpId;
    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;
    private String name;
    private BigDecimal capacity;
    private BigDecimal currentDip;
}
