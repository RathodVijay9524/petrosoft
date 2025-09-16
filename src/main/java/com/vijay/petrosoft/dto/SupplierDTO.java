package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.Supplier;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {
    
    private Long id;
    
    @NotBlank(message = "Supplier code is required")
    @Size(max = 20, message = "Supplier code must not exceed 20 characters")
    private String supplierCode;
    
    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Supplier name must not exceed 200 characters")
    private String supplierName;
    
    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Size(max = 20, message = "Mobile must not exceed 20 characters")
    private String mobile;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;
    
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;
    
    @Size(max = 10, message = "Pincode must not exceed 10 characters")
    private String pincode;
    
    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;
    
    @Size(max = 20, message = "GST number must not exceed 20 characters")
    private String gstNumber;
    
    @Size(max = 20, message = "PAN number must not exceed 20 characters")
    private String panNumber;
    
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;
    
    @Size(max = 30, message = "Bank account number must not exceed 30 characters")
    private String bankAccountNumber;
    
    @Size(max = 15, message = "Bank IFSC must not exceed 15 characters")
    private String bankIfsc;
    
    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    private Double creditLimit;
    
    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    private String paymentTerms;
    
    private Supplier.Status status;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @NotNull(message = "Pump ID is required")
    private Long pumpId;
    
    // Additional fields for UI
    private String statusDisplay;
    private String fullAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
