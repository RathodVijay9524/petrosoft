package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchase_bills")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@EqualsAndHashCode(callSuper = false)
public class PurchaseBill extends Auditable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bill_number", length = 50, nullable = false, unique = true)
    private String billNumber;
    
    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @Column(name = "pump_id", nullable = false)
    private Long pumpId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "bill_type", nullable = false)
    private BillType billType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;
    
    @Column(name = "sub_total", precision = 15, scale = 2)
    private BigDecimal subTotal;
    
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;
    
    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;
    
    @Column(name = "driver_name", length = 100)
    private String driverName;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @OneToMany(mappedBy = "purchaseBill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PurchaseBillItem> items;
    
    public enum BillType {
        FUEL_PURCHASE,
        EQUIPMENT_PURCHASE,
        SERVICE_PURCHASE,
        MAINTENANCE_PURCHASE,
        OTHER_PURCHASE
    }
    
    public enum Status {
        DRAFT,
        SUBMITTED,
        APPROVED,
        RECEIVED,
        INVOICED,
        PAID,
        CANCELLED
    }
    
    public enum PaymentStatus {
        PENDING,
        PARTIAL,
        PAID,
        OVERDUE,
        CANCELLED
    }
    
    // Helper methods
    public void calculateTotal() {
        if (subTotal != null && taxAmount != null && discountAmount != null) {
            this.totalAmount = subTotal.add(taxAmount).subtract(discountAmount);
        }
    }
    
    public boolean isApproved() {
        return Status.APPROVED.equals(status);
    }
    
    public boolean isPaid() {
        return PaymentStatus.PAID.equals(paymentStatus);
    }
}
