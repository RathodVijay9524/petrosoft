package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Nozzle;
import com.vijay.petrosoft.dto.NozzleDTO;
import com.vijay.petrosoft.repository.NozzleRepository;
import com.vijay.petrosoft.service.NozzleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NozzleServiceImpl implements NozzleService {

    private final NozzleRepository nozzleRepository;

    @Override
    public NozzleDTO createNozzle(NozzleDTO nozzleDTO) {
        if (isDispenserCodeExists(nozzleDTO.getPumpId(), nozzleDTO.getDispenserCode())) {
            throw new RuntimeException("Dispenser code already exists for pump: " + nozzleDTO.getPumpId());
        }

        Nozzle nozzle = Nozzle.builder()
                .pumpId(nozzleDTO.getPumpId())
                .name(nozzleDTO.getName())
                .dispenserCode(nozzleDTO.getDispenserCode())
                .build();

        Nozzle savedNozzle = nozzleRepository.save(nozzle);
        return convertToDTO(savedNozzle);
    }

    @Override
    public NozzleDTO updateNozzle(Long id, NozzleDTO nozzleDTO) {
        Nozzle nozzle = nozzleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nozzle not found with id: " + id));

        nozzle.setPumpId(nozzleDTO.getPumpId());
        nozzle.setName(nozzleDTO.getName());
        nozzle.setDispenserCode(nozzleDTO.getDispenserCode());

        Nozzle updatedNozzle = nozzleRepository.save(nozzle);
        return convertToDTO(updatedNozzle);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NozzleDTO> getNozzleById(Long id) {
        return nozzleRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NozzleDTO> getAllNozzles() {
        return nozzleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNozzle(Long id) {
        if (!nozzleRepository.existsById(id)) {
            throw new RuntimeException("Nozzle not found with id: " + id);
        }
        nozzleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NozzleDTO> getNozzlesByPumpId(Long pumpId) {
        return nozzleRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NozzleDTO> getNozzlesByFuelType(Long fuelTypeId) {
        return nozzleRepository.findAll().stream()
                .filter(nozzle -> nozzle.getFuelType() != null && nozzle.getFuelType().getId().equals(fuelTypeId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NozzleDTO getNozzleByPumpAndDispenserCode(Long pumpId, String dispenserCode) {
        return nozzleRepository.findByPumpId(pumpId).stream()
                .filter(nozzle -> dispenserCode.equals(nozzle.getDispenserCode()))
                .findFirst()
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Nozzle not found for pump: " + pumpId + " with dispenser code: " + dispenserCode));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDispenserCodeExists(Long pumpId, String dispenserCode) {
        return nozzleRepository.findByPumpId(pumpId).stream()
                .anyMatch(nozzle -> dispenserCode.equals(nozzle.getDispenserCode()));
    }

    private NozzleDTO convertToDTO(Nozzle nozzle) {
        return NozzleDTO.builder()
                .id(nozzle.getId())
                .pumpId(nozzle.getPumpId())
                .fuelTypeId(nozzle.getFuelType() != null ? nozzle.getFuelType().getId() : null)
                .name(nozzle.getName())
                .dispenserCode(nozzle.getDispenserCode())
                .build();
    }
}
