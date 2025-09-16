package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nozzles")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Nozzle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pumpId;
    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;
    private String name;
    private String dispenserCode;
}
