package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fuel_types")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FuelType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String uom = "L";
}
