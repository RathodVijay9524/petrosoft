package com.vijay.petrosoft.dto;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerDTO {
    private Long id;
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String name;
    
    @Size(max = 20, message = "Customer code must not exceed 20 characters")
    private String code;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String phone;
    
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
    
    @DecimalMin(value = "0.0", message = "Outstanding amount cannot be negative")
    private BigDecimal outstanding;
}
    
