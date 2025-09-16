package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode(callSuper=false)
public class Customer extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private String phone;
    private String email;
    private String address;
    private BigDecimal outstanding = BigDecimal.ZERO;
}
