package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Shift;
import com.vijay.petrosoft.dto.ShiftDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftService {
    ShiftDTO createShift(ShiftDTO shiftDTO);
    ShiftDTO updateShift(Long id, ShiftDTO shiftDTO);
    Optional<ShiftDTO> getShiftById(Long id);
    List<ShiftDTO> getAllShifts();
    void deleteShift(Long id);
    ShiftDTO openShift(Long pumpId, Long operatorId);
    ShiftDTO closeShift(Long shiftId);
    List<ShiftDTO> getShiftsByPumpId(Long pumpId);
    List<ShiftDTO> getShiftsByOperatorId(Long operatorId);
    List<ShiftDTO> getActiveShifts();
    List<ShiftDTO> getShiftsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    ShiftDTO getCurrentShiftByPump(Long pumpId);
    boolean isShiftActive(Long shiftId);
}
