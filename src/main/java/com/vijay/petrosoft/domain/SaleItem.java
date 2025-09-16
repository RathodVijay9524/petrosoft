package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SaleItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_transaction_id", nullable = false)
    private SaleTransaction saleTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nozzle_id")
    private Nozzle nozzle;

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Column(name = "item_code", length = 50)
    private String itemCode;

    @Column(name = "quantity", precision = 10, scale = 3, nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit", length = 20, nullable = false)
    private String unit; // LITRES, KILOGRAMS, etc.

    @Column(name = "rate", precision = 8, scale = 2, nullable = false)
    private BigDecimal rate;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "notes", length = 500)
    private String notes;

    // Helper methods
    public void calculateAmount() {
        if (quantity != null && rate != null) {
            this.amount = quantity.multiply(rate);
        }
    }

    public void calculateTotalAmount() {
        if (amount != null && discountAmount != null && taxAmount != null) {
            this.totalAmount = amount.subtract(discountAmount).add(taxAmount);
        }
    }

    public void calculateDiscountAmount() {
        if (amount != null && discountPercentage != null) {
            this.discountAmount = amount.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        }
    }

    public void calculateTaxAmount() {
        BigDecimal taxableAmount = amount.subtract(discountAmount);
        if (taxableAmount != null && taxPercentage != null) {
            this.taxAmount = taxableAmount.multiply(taxPercentage).divide(BigDecimal.valueOf(100));
        }
    }
}
