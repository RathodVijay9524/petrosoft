package com.vijay.petrosoft.dto;

import com.vijay.petrosoft.domain.Tank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TankDTO {
    private Long id;
    private String name;
    private String code;
    private BigDecimal capacity;
    private BigDecimal currentDip;  // Added for TankServiceImpl compatibility
    private BigDecimal currentLevel;
    private BigDecimal minimumLevel;
    private BigDecimal maximumLevel;
    private String fuelType;
    private Long fuelTypeId;  // Added for TankServiceImpl compatibility
    private Long pumpId;
    private String pumpName;
    private Boolean isActive;
    private String status;
    private LocalDateTime lastUpdated;
    private String location;
    private String description;
    private BigDecimal temperature;
    private BigDecimal density;
    private String unit;
    private Boolean isOnline;
    private LocalDateTime lastMaintenance;
    private LocalDateTime nextMaintenance;
    private String maintenanceNotes;
    private BigDecimal costPerLiter;
    private BigDecimal sellingPricePerLiter;
    private BigDecimal profitMargin;
    private String supplier;
    private String batchNumber;
    private LocalDateTime expiryDate;
    private String qualityGrade;
    private Boolean isCalibrated;
    private LocalDateTime lastCalibration;
    private BigDecimal calibrationFactor;
    private String calibrationCertificate;
    private String notes;
}