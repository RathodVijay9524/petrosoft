package com.vijay.petrosoft.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentDTO {
    private Long id;
    
    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;
    
    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;
    
    @Size(max = 100, message = "Transaction ID must not exceed 100 characters")
    private String transactionId;
    
    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "PENDING|COMPLETED|FAILED|REFUNDED", message = "Invalid payment status")
    private String status;
    
    private LocalDateTime paymentDate;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    private String gatewayResponse;
    
    @Size(max = 100, message = "Gateway transaction ID must not exceed 100 characters")
    private String gatewayTransactionId;
}
