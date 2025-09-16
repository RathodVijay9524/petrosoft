package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Tank;
import com.vijay.petrosoft.dto.TankDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TankService {
    TankDTO createTank(TankDTO tankDTO);
    TankDTO updateTank(Long id, TankDTO tankDTO);
    Optional<TankDTO> getTankById(Long id);
    List<TankDTO> getAllTanks();
    void deleteTank(Long id);
    List<TankDTO> getTanksByPumpId(Long pumpId);
    List<TankDTO> getTanksByFuelType(Long fuelTypeId);
    TankDTO updateTankDip(Long tankId, BigDecimal newDip);
    TankDTO getTankByPumpAndFuelType(Long pumpId, Long fuelTypeId);
    boolean isTankCapacityExceeded(Long tankId, BigDecimal additionalQuantity);
}
