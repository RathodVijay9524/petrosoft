package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "subscription_plans")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SubscriptionPlan extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle;
    
    @Builder.Default
    private boolean active = true;
    
    @ElementCollection
    @CollectionTable(name = "subscription_plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features;
    
    private Integer maxPumps;
    private Integer maxUsers;
    
    @Builder.Default
    private boolean supportIncluded = false;
    
    @Builder.Default
    private boolean reportingIncluded = false;
    
    @Builder.Default
    private boolean apiAccess = false;
    
    public enum BillingCycle {
        MONTHLY, QUARTERLY, ANNUAL
    }
}
