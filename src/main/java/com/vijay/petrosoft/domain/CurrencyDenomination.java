package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "currency_denominations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CurrencyDenomination extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pump_id", nullable = false)
    private Long pumpId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type", nullable = false)
    private CurrencyType currencyType;

    @Column(name = "denomination_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal denominationValue;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "shift_id", nullable = false)
    private Long shiftId;

    @Column(name = "counted_by")
    private Long countedBy;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "notes", length = 500)
    private String notes;

    public enum CurrencyType {
        NOTE_2000,
        NOTE_500,
        NOTE_200,
        NOTE_100,
        NOTE_50,
        NOTE_20,
        NOTE_10,
        COIN_10,
        COIN_5,
        COIN_2,
        COIN_1,
        OTHER_COINS
    }

    // Helper method to calculate total amount
    public void calculateTotalAmount() {
        if (denominationValue != null && quantity != null) {
            this.totalAmount = denominationValue.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
