package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.ShiftDTO;
import com.vijay.petrosoft.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping
    public ResponseEntity<ShiftDTO> createShift(@RequestBody ShiftDTO shiftDTO) {
        try {
            ShiftDTO createdShift = shiftService.createShift(shiftDTO);
            return new ResponseEntity<>(createdShift, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftDTO> getShiftById(@PathVariable Long id) {
        return shiftService.getShiftById(id)
                .map(shift -> new ResponseEntity<>(shift, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ShiftDTO>> getAllShifts() {
        List<ShiftDTO> shifts = shiftService.getAllShifts();
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftDTO> updateShift(@PathVariable Long id, @RequestBody ShiftDTO shiftDTO) {
        try {
            ShiftDTO updatedShift = shiftService.updateShift(id, shiftDTO);
            return new ResponseEntity<>(updatedShift, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        try {
            shiftService.deleteShift(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/open")
    public ResponseEntity<ShiftDTO> openShift(@RequestParam Long pumpId, @RequestParam Long operatorId) {
        try {
            ShiftDTO shift = shiftService.openShift(pumpId, operatorId);
            return new ResponseEntity<>(shift, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{shiftId}/close")
    public ResponseEntity<ShiftDTO> closeShift(@PathVariable Long shiftId) {
        try {
            ShiftDTO shift = shiftService.closeShift(shiftId);
            return new ResponseEntity<>(shift, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<ShiftDTO>> getShiftsByPumpId(@PathVariable Long pumpId) {
        List<ShiftDTO> shifts = shiftService.getShiftsByPumpId(pumpId);
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    @GetMapping("/operator/{operatorId}")
    public ResponseEntity<List<ShiftDTO>> getShiftsByOperatorId(@PathVariable Long operatorId) {
        List<ShiftDTO> shifts = shiftService.getShiftsByOperatorId(operatorId);
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ShiftDTO>> getActiveShifts() {
        List<ShiftDTO> shifts = shiftService.getActiveShifts();
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ShiftDTO>> getShiftsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ShiftDTO> shifts = shiftService.getShiftsByDateRange(startDate, endDate);
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    @GetMapping("/current/pump/{pumpId}")
    public ResponseEntity<ShiftDTO> getCurrentShiftByPump(@PathVariable Long pumpId) {
        ShiftDTO shift = shiftService.getCurrentShiftByPump(pumpId);
        if (shift != null) {
            return new ResponseEntity<>(shift, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{shiftId}/is-active")
    public ResponseEntity<Boolean> isShiftActive(@PathVariable Long shiftId) {
        boolean isActive = shiftService.isShiftActive(shiftId);
        return new ResponseEntity<>(isActive, HttpStatus.OK);
    }
}
