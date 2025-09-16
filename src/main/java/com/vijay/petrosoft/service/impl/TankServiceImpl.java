package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Tank;
import com.vijay.petrosoft.dto.TankDTO;
import com.vijay.petrosoft.repository.TankRepository;
import com.vijay.petrosoft.service.TankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TankServiceImpl implements TankService {

    private final TankRepository tankRepository;

    @Override
    public TankDTO createTank(TankDTO tankDTO) {
        Tank tank = Tank.builder()
                .pumpId(tankDTO.getPumpId())
                .name(tankDTO.getName())
                .capacity(tankDTO.getCapacity())
                .currentDip(tankDTO.getCurrentDip())
                .build();

        Tank savedTank = tankRepository.save(tank);
        return convertToDTO(savedTank);
    }

    @Override
    public TankDTO updateTank(Long id, TankDTO tankDTO) {
        Tank tank = tankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tank not found with id: " + id));

        tank.setPumpId(tankDTO.getPumpId());
        tank.setName(tankDTO.getName());
        tank.setCapacity(tankDTO.getCapacity());
        tank.setCurrentDip(tankDTO.getCurrentDip());

        Tank updatedTank = tankRepository.save(tank);
        return convertToDTO(updatedTank);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TankDTO> getTankById(Long id) {
        return tankRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TankDTO> getAllTanks() {
        return tankRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTank(Long id) {
        if (!tankRepository.existsById(id)) {
            throw new RuntimeException("Tank not found with id: " + id);
        }
        tankRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TankDTO> getTanksByPumpId(Long pumpId) {
        return tankRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TankDTO> getTanksByFuelType(Long fuelTypeId) {
        return tankRepository.findAll().stream()
                .filter(tank -> tank.getFuelType() != null && tank.getFuelType().getId().equals(fuelTypeId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TankDTO updateTankDip(Long tankId, BigDecimal newDip) {
        Tank tank = tankRepository.findById(tankId)
                .orElseThrow(() -> new RuntimeException("Tank not found with id: " + tankId));

        tank.setCurrentDip(newDip);
        Tank updatedTank = tankRepository.save(tank);
        return convertToDTO(updatedTank);
    }

    @Override
    @Transactional(readOnly = true)
    public TankDTO getTankByPumpAndFuelType(Long pumpId, Long fuelTypeId) {
        return tankRepository.findByPumpId(pumpId).stream()
                .filter(tank -> tank.getFuelType() != null && tank.getFuelType().getId().equals(fuelTypeId))
                .findFirst()
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Tank not found for pump: " + pumpId + " and fuel type: " + fuelTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTankCapacityExceeded(Long tankId, BigDecimal additionalQuantity) {
        Tank tank = tankRepository.findById(tankId)
                .orElseThrow(() -> new RuntimeException("Tank not found with id: " + tankId));

        BigDecimal currentLevel = tank.getCurrentDip() != null ? tank.getCurrentDip() : BigDecimal.ZERO;
        BigDecimal newLevel = currentLevel.add(additionalQuantity);
        
        return newLevel.compareTo(tank.getCapacity()) > 0;
    }

    private TankDTO convertToDTO(Tank tank) {
        return TankDTO.builder()
                .id(tank.getId())
                .pumpId(tank.getPumpId())
                .fuelTypeId(tank.getFuelType() != null ? tank.getFuelType().getId() : null)
                .name(tank.getName())
                .capacity(tank.getCapacity())
                .currentDip(tank.getCurrentDip())
                .build();
    }
}
