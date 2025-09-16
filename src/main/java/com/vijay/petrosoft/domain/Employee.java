package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Data @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode(callSuper=false)
public class Employee extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employeeCode;
    private String name;
    private String role;
    private String phone;
    private String email;
}
