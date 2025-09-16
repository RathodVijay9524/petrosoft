package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@EqualsAndHashCode(callSuper = false)
public class Supplier extends Auditable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "supplier_code", length = 20, nullable = false, unique = true)
    private String supplierCode;
    
    @Column(name = "supplier_name", length = 200, nullable = false)
    private String supplierName;
    
    @Column(name = "contact_person", length = 100)
    private String contactPerson;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "mobile", length = 20)
    private String mobile;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "city", length = 50)
    private String city;
    
    @Column(name = "state", length = 50)
    private String state;
    
    @Column(name = "pincode", length = 10)
    private String pincode;
    
    @Column(name = "country", length = 50)
    private String country;
    
    @Column(name = "gst_number", length = 20)
    private String gstNumber;
    
    @Column(name = "pan_number", length = 20)
    private String panNumber;
    
    @Column(name = "bank_name", length = 100)
    private String bankName;
    
    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;
    
    @Column(name = "bank_ifsc", length = 15)
    private String bankIfsc;
    
    @Column(name = "credit_limit")
    private Double creditLimit;
    
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "pump_id", nullable = false)
    private Long pumpId;
    
    public enum Status {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        BLACKLISTED
    }
    
    // Helper methods
    public boolean isActive() {
        return Status.ACTIVE.equals(status);
    }
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (this.address != null) address.append(this.address);
        if (city != null) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (state != null) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }
        if (pincode != null) {
            if (address.length() > 0) address.append(" - ");
            address.append(pincode);
        }
        return address.toString();
    }
}
