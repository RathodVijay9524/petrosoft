package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@EqualsAndHashCode(callSuper = false)
public class SaleTransaction extends Auditable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pump_id", nullable = false)
    private Long pumpId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nozzle_id")
    private Nozzle nozzle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @Column(name = "sale_number", length = 50, nullable = false, unique = true)
    private String saleNumber;
    
    @Column(name = "quantity", precision = 10, scale = 3, nullable = false)
    private BigDecimal quantity;
    
    @Column(name = "rate", precision = 8, scale = 2, nullable = false)
    private BigDecimal rate;
    
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type", nullable = false)
    private SaleType saleType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.COMPLETED;
    
    @Column(name = "transacted_at", nullable = false)
    private LocalDateTime transactedAt;
    
    @Column(name = "operator_id")
    private Long operatorId;
    
    @Column(name = "cashier_id")
    private Long cashierId;
    
    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;
    
    @Column(name = "driver_name", length = 100)
    private String driverName;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;
    
    @Column(name = "card_type", length = 20)
    private String cardType;
    
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;
    
    public enum PaymentMethod {
        CASH,
        CARD,
        CREDIT,
        WALLET,
        UPI,
        NET_BANKING
    }
    
    public enum SaleType {
        RETAIL,
        BULK,
        WHOLESALE,
        GOVERNMENT,
        STAFF
    }
    
    public enum Status {
        PENDING,
        COMPLETED,
        CANCELLED,
        REFUNDED,
        PARTIALLY_REFUNDED
    }
    
    // Helper methods
    public void calculateTotalAmount() {
        if (amount != null && discountAmount != null && taxAmount != null) {
            this.totalAmount = amount.subtract(discountAmount).add(taxAmount);
        }
    }
    
    public boolean isCreditSale() {
        return PaymentMethod.CREDIT.equals(paymentMethod);
    }
    
    public boolean isCardSale() {
        return PaymentMethod.CARD.equals(paymentMethod);
    }
    
    public boolean isCashSale() {
        return PaymentMethod.CASH.equals(paymentMethod);
    }
}
