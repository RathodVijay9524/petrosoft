package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.FuelType;
import com.vijay.petrosoft.dto.FuelTypeDTO;
import com.vijay.petrosoft.repository.FuelTypeRepository;
import com.vijay.petrosoft.service.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FuelTypeServiceImpl implements FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;

    @Override
    public FuelTypeDTO createFuelType(FuelTypeDTO fuelTypeDTO) {
        if (isFuelTypeNameExists(fuelTypeDTO.getName())) {
            throw new RuntimeException("Fuel type name already exists: " + fuelTypeDTO.getName());
        }

        FuelType fuelType = FuelType.builder()
                .name(fuelTypeDTO.getName())
                .uom(fuelTypeDTO.getUom())
                .build();

        FuelType savedFuelType = fuelTypeRepository.save(fuelType);
        return convertToDTO(savedFuelType);
    }

    @Override
    public FuelTypeDTO updateFuelType(Long id, FuelTypeDTO fuelTypeDTO) {
        FuelType fuelType = fuelTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fuel type not found with id: " + id));

        fuelType.setName(fuelTypeDTO.getName());
        fuelType.setUom(fuelTypeDTO.getUom());

        FuelType updatedFuelType = fuelTypeRepository.save(fuelType);
        return convertToDTO(updatedFuelType);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FuelTypeDTO> getFuelTypeById(Long id) {
        return fuelTypeRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelTypeDTO> getAllFuelTypes() {
        return fuelTypeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFuelType(Long id) {
        if (!fuelTypeRepository.existsById(id)) {
            throw new RuntimeException("Fuel type not found with id: " + id);
        }
        fuelTypeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFuelTypeNameExists(String name) {
        return fuelTypeRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public FuelTypeDTO getFuelTypeByName(String name) {
        FuelType fuelType = fuelTypeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Fuel type not found with name: " + name));
        return convertToDTO(fuelType);
    }

    private FuelTypeDTO convertToDTO(FuelType fuelType) {
        return FuelTypeDTO.builder()
                .id(fuelType.getId())
                .name(fuelType.getName())
                .uom(fuelType.getUom())
                .build();
    }
}
