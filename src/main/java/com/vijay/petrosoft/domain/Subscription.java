package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "subscriptions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode(callSuper=false)
public class Subscription extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long pumpId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;
    
    private String planName;
    private LocalDate startsAt;
    private LocalDate endsAt;
    
    @Builder.Default
    private boolean active = true;
    
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    private LocalDate lastPaymentDate;
    private LocalDate nextPaymentDate;
    
    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;
    
    @Column(length = 500)
    private String description;
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;
    
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, EXPIRED, CANCELLED
    }
    
    public enum BillingCycle {
        MONTHLY, QUARTERLY, ANNUAL
    }
}
