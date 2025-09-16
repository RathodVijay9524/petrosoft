package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Shift extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pumpId;
    private Long operatorId;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private String status;
}
