package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Shift;
import com.vijay.petrosoft.dto.ShiftDTO;
import com.vijay.petrosoft.repository.ShiftRepository;
import com.vijay.petrosoft.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    @Override
    public ShiftDTO createShift(ShiftDTO shiftDTO) {
        Shift shift = Shift.builder()
                .pumpId(shiftDTO.getPumpId())
                .operatorId(shiftDTO.getOperatorId())
                .openedAt(shiftDTO.getOpenedAt())
                .closedAt(shiftDTO.getClosedAt())
                .status(shiftDTO.getStatus())
                .build();

        Shift savedShift = shiftRepository.save(shift);
        return convertToDTO(savedShift);
    }

    @Override
    public ShiftDTO updateShift(Long id, ShiftDTO shiftDTO) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));

        shift.setPumpId(shiftDTO.getPumpId());
        shift.setOperatorId(shiftDTO.getOperatorId());
        shift.setOpenedAt(shiftDTO.getOpenedAt());
        shift.setClosedAt(shiftDTO.getClosedAt());
        shift.setStatus(shiftDTO.getStatus());

        Shift updatedShift = shiftRepository.save(shift);
        return convertToDTO(updatedShift);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShiftDTO> getShiftById(Long id) {
        return shiftRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDTO> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteShift(Long id) {
        if (!shiftRepository.existsById(id)) {
            throw new RuntimeException("Shift not found with id: " + id);
        }
        shiftRepository.deleteById(id);
    }

    @Override
    public ShiftDTO openShift(Long pumpId, Long operatorId) {
        // Check if there's already an active shift for this pump
        List<Shift> activeShifts = shiftRepository.findByPumpIdAndStatus(pumpId, "ACTIVE");
        if (!activeShifts.isEmpty()) {
            throw new RuntimeException("There is already an active shift for pump: " + pumpId);
        }

        Shift shift = Shift.builder()
                .pumpId(pumpId)
                .operatorId(operatorId)
                .openedAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        Shift savedShift = shiftRepository.save(shift);
        return convertToDTO(savedShift);
    }

    @Override
    public ShiftDTO closeShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + shiftId));

        if (!"ACTIVE".equals(shift.getStatus())) {
            throw new RuntimeException("Cannot close a shift that is not active");
        }

        shift.setClosedAt(LocalDateTime.now());
        shift.setStatus("CLOSED");

        Shift updatedShift = shiftRepository.save(shift);
        return convertToDTO(updatedShift);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDTO> getShiftsByPumpId(Long pumpId) {
        return shiftRepository.findByPumpId(pumpId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDTO> getShiftsByOperatorId(Long operatorId) {
        return shiftRepository.findByOperatorId(operatorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDTO> getActiveShifts() {
        return shiftRepository.findByStatus("ACTIVE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDTO> getShiftsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return shiftRepository.findByOpenedAtBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftDTO getCurrentShiftByPump(Long pumpId) {
        List<Shift> activeShifts = shiftRepository.findByPumpIdAndStatus(pumpId, "ACTIVE");
        if (activeShifts.isEmpty()) {
            return null;
        }
        return convertToDTO(activeShifts.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isShiftActive(Long shiftId) {
        return shiftRepository.findById(shiftId)
                .map(shift -> "ACTIVE".equals(shift.getStatus()))
                .orElse(false);
    }

    private ShiftDTO convertToDTO(Shift shift) {
        return ShiftDTO.builder()
                .id(shift.getId())
                .pumpId(shift.getPumpId())
                .operatorId(shift.getOperatorId())
                .openedAt(shift.getOpenedAt())
                .closedAt(shift.getClosedAt())
                .status(shift.getStatus())
                .build();
    }
}
