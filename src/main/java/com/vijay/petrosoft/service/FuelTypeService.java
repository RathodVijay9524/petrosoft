package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.FuelType;
import com.vijay.petrosoft.dto.FuelTypeDTO;
import java.util.List;
import java.util.Optional;

public interface FuelTypeService {
    FuelTypeDTO createFuelType(FuelTypeDTO fuelTypeDTO);
    FuelTypeDTO updateFuelType(Long id, FuelTypeDTO fuelTypeDTO);
    Optional<FuelTypeDTO> getFuelTypeById(Long id);
    List<FuelTypeDTO> getAllFuelTypes();
    void deleteFuelType(Long id);
    boolean isFuelTypeNameExists(String name);
    FuelTypeDTO getFuelTypeByName(String name);
}
