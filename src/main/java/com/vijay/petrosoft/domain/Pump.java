package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pumps")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Pump extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private String address;
    private String city;
    private String state;
    private String gstNumber;
    private String contactPhone;
}
