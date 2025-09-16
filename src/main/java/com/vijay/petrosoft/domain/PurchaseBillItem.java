package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_bill_items")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@EqualsAndHashCode(callSuper = false)
public class PurchaseBillItem extends Auditable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_bill_id", nullable = false)
    private PurchaseBill purchaseBill;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id")
    private FuelType fuelType;
    
    @Column(name = "item_description", length = 500, nullable = false)
    private String itemDescription;
    
    @Column(name = "quantity", precision = 10, scale = 3, nullable = false)
    private BigDecimal quantity;
    
    @Column(name = "unit_price", precision = 8, scale = 2, nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;
    
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;
    
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "net_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal netAmount;
    
    @Column(name = "received_quantity", precision = 10, scale = 3)
    private BigDecimal receivedQuantity;
    
    @Column(name = "remaining_quantity", precision = 10, scale = 3)
    private BigDecimal remainingQuantity;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    // Helper methods
    public void calculateTotals() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = quantity.multiply(unitPrice);
            
            // Calculate discount
            if (discountRate != null) {
                this.discountAmount = totalPrice.multiply(discountRate.divide(BigDecimal.valueOf(100)));
            } else if (discountAmount == null) {
                this.discountAmount = BigDecimal.ZERO;
            }
            
            // Calculate tax on discounted amount
            BigDecimal discountedAmount = totalPrice.subtract(discountAmount);
            if (taxRate != null) {
                this.taxAmount = discountedAmount.multiply(taxRate.divide(BigDecimal.valueOf(100)));
            } else if (taxAmount == null) {
                this.taxAmount = BigDecimal.ZERO;
            }
            
            this.netAmount = discountedAmount.add(taxAmount);
        }
        
        // Calculate remaining quantity
        if (quantity != null && receivedQuantity != null) {
            this.remainingQuantity = quantity.subtract(receivedQuantity);
        } else if (quantity != null) {
            this.remainingQuantity = quantity;
        }
    }
    
    public boolean isFullyReceived() {
        return receivedQuantity != null && receivedQuantity.compareTo(quantity) >= 0;
    }
    
    public boolean isPartiallyReceived() {
        return receivedQuantity != null && receivedQuantity.compareTo(BigDecimal.ZERO) > 0 && !isFullyReceived();
    }
}
