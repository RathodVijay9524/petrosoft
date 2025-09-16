package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Nozzle;
import com.vijay.petrosoft.dto.NozzleDTO;
import java.util.List;
import java.util.Optional;

public interface NozzleService {
    NozzleDTO createNozzle(NozzleDTO nozzleDTO);
    NozzleDTO updateNozzle(Long id, NozzleDTO nozzleDTO);
    Optional<NozzleDTO> getNozzleById(Long id);
    List<NozzleDTO> getAllNozzles();
    void deleteNozzle(Long id);
    List<NozzleDTO> getNozzlesByPumpId(Long pumpId);
    List<NozzleDTO> getNozzlesByFuelType(Long fuelTypeId);
    NozzleDTO getNozzleByPumpAndDispenserCode(Long pumpId, String dispenserCode);
    boolean isDispenserCodeExists(Long pumpId, String dispenserCode);
}
