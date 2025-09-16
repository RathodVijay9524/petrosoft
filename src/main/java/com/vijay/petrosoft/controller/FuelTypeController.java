package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.FuelTypeDTO;
import com.vijay.petrosoft.service.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel-types")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @PostMapping
    public ResponseEntity<FuelTypeDTO> createFuelType(@RequestBody FuelTypeDTO fuelTypeDTO) {
        try {
            FuelTypeDTO createdFuelType = fuelTypeService.createFuelType(fuelTypeDTO);
            return new ResponseEntity<>(createdFuelType, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelTypeDTO> getFuelTypeById(@PathVariable Long id) {
        return fuelTypeService.getFuelTypeById(id)
                .map(fuelType -> new ResponseEntity<>(fuelType, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<FuelTypeDTO>> getAllFuelTypes() {
        List<FuelTypeDTO> fuelTypes = fuelTypeService.getAllFuelTypes();
        return new ResponseEntity<>(fuelTypes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuelTypeDTO> updateFuelType(@PathVariable Long id, @RequestBody FuelTypeDTO fuelTypeDTO) {
        try {
            FuelTypeDTO updatedFuelType = fuelTypeService.updateFuelType(id, fuelTypeDTO);
            return new ResponseEntity<>(updatedFuelType, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFuelType(@PathVariable Long id) {
        try {
            fuelTypeService.deleteFuelType(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<FuelTypeDTO> getFuelTypeByName(@PathVariable String name) {
        try {
            FuelTypeDTO fuelType = fuelTypeService.getFuelTypeByName(name);
            return new ResponseEntity<>(fuelType, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
