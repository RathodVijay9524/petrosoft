package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "financial_years")
@Data @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode(callSuper=false)
public class FinancialYear extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // e.g., "FY 2024-25"
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Builder.Default
    private boolean active = false; // Only one can be active at a time
    
    @Column(length = 500)
    private String description;
    
    private Long pumpId; // Null means global financial year
    
    // Business methods
    public boolean isCurrentYear() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    
    public boolean isPastYear() {
        return endDate.isBefore(LocalDate.now());
    }
    
    public boolean isFutureYear() {
        return startDate.isAfter(LocalDate.now());
    }
}
