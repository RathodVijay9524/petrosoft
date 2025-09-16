package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Pump;
import com.vijay.petrosoft.dto.PumpDTO;
import java.util.List;
import java.util.Optional;

public interface PumpService {
    PumpDTO createPump(PumpDTO pumpDTO);
    PumpDTO updatePump(Long id, PumpDTO pumpDTO);
    Optional<PumpDTO> getPumpById(Long id);
    List<PumpDTO> getAllPumps();
    void deletePump(Long id);
    List<PumpDTO> getPumpsByCity(String city);
    List<PumpDTO> getPumpsByState(String state);
    boolean isGstNumberExists(String gstNumber);
    PumpDTO getPumpByGstNumber(String gstNumber);
}
