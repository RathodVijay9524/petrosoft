package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.TankDTO;
import com.vijay.petrosoft.service.TankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tanks")
@RequiredArgsConstructor
public class TankController {

    private final TankService tankService;

    @PostMapping
    public ResponseEntity<TankDTO> createTank(@RequestBody TankDTO tankDTO) {
        try {
            TankDTO createdTank = tankService.createTank(tankDTO);
            return new ResponseEntity<>(createdTank, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TankDTO> getTankById(@PathVariable Long id) {
        return tankService.getTankById(id)
                .map(tank -> new ResponseEntity<>(tank, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<TankDTO>> getAllTanks() {
        List<TankDTO> tanks = tankService.getAllTanks();
        return new ResponseEntity<>(tanks, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TankDTO> updateTank(@PathVariable Long id, @RequestBody TankDTO tankDTO) {
        try {
            TankDTO updatedTank = tankService.updateTank(id, tankDTO);
            return new ResponseEntity<>(updatedTank, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTank(@PathVariable Long id) {
        try {
            tankService.deleteTank(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<TankDTO>> getTanksByPumpId(@PathVariable Long pumpId) {
        List<TankDTO> tanks = tankService.getTanksByPumpId(pumpId);
        return new ResponseEntity<>(tanks, HttpStatus.OK);
    }

    @GetMapping("/fuel-type/{fuelTypeId}")
    public ResponseEntity<List<TankDTO>> getTanksByFuelType(@PathVariable Long fuelTypeId) {
        List<TankDTO> tanks = tankService.getTanksByFuelType(fuelTypeId);
        return new ResponseEntity<>(tanks, HttpStatus.OK);
    }

    @PutMapping("/{tankId}/dip")
    public ResponseEntity<TankDTO> updateTankDip(
            @PathVariable Long tankId,
            @RequestParam BigDecimal newDip) {
        try {
            TankDTO tank = tankService.updateTankDip(tankId, newDip);
            return new ResponseEntity<>(tank, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pump/{pumpId}/fuel-type/{fuelTypeId}")
    public ResponseEntity<TankDTO> getTankByPumpAndFuelType(
            @PathVariable Long pumpId,
            @PathVariable Long fuelTypeId) {
        try {
            TankDTO tank = tankService.getTankByPumpAndFuelType(pumpId, fuelTypeId);
            return new ResponseEntity<>(tank, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{tankId}/capacity-check")
    public ResponseEntity<Boolean> isTankCapacityExceeded(
            @PathVariable Long tankId,
            @RequestParam BigDecimal additionalQuantity) {
        try {
            boolean exceeded = tankService.isTankCapacityExceeded(tankId, additionalQuantity);
            return new ResponseEntity<>(exceeded, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
