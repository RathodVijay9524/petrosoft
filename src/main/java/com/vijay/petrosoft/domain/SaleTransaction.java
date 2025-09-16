package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SaleTransaction extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pumpId;
    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;
    @ManyToOne
    @JoinColumn(name = "nozzle_id")
    private Nozzle nozzle;
    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;
    private BigDecimal quantity;
    private BigDecimal rate;
    private BigDecimal amount;
    private boolean creditSale;
    private Long customerId;
    private LocalDateTime transactedAt;
}
