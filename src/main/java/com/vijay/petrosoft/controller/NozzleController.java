package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.NozzleDTO;
import com.vijay.petrosoft.service.NozzleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nozzles")
@RequiredArgsConstructor
public class NozzleController {

    private final NozzleService nozzleService;

    @PostMapping
    public ResponseEntity<NozzleDTO> createNozzle(@RequestBody NozzleDTO nozzleDTO) {
        try {
            NozzleDTO createdNozzle = nozzleService.createNozzle(nozzleDTO);
            return new ResponseEntity<>(createdNozzle, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NozzleDTO> getNozzleById(@PathVariable Long id) {
        return nozzleService.getNozzleById(id)
                .map(nozzle -> new ResponseEntity<>(nozzle, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<NozzleDTO>> getAllNozzles() {
        List<NozzleDTO> nozzles = nozzleService.getAllNozzles();
        return new ResponseEntity<>(nozzles, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NozzleDTO> updateNozzle(@PathVariable Long id, @RequestBody NozzleDTO nozzleDTO) {
        try {
            NozzleDTO updatedNozzle = nozzleService.updateNozzle(id, nozzleDTO);
            return new ResponseEntity<>(updatedNozzle, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNozzle(@PathVariable Long id) {
        try {
            nozzleService.deleteNozzle(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<NozzleDTO>> getNozzlesByPumpId(@PathVariable Long pumpId) {
        List<NozzleDTO> nozzles = nozzleService.getNozzlesByPumpId(pumpId);
        return new ResponseEntity<>(nozzles, HttpStatus.OK);
    }

    @GetMapping("/fuel-type/{fuelTypeId}")
    public ResponseEntity<List<NozzleDTO>> getNozzlesByFuelType(@PathVariable Long fuelTypeId) {
        List<NozzleDTO> nozzles = nozzleService.getNozzlesByFuelType(fuelTypeId);
        return new ResponseEntity<>(nozzles, HttpStatus.OK);
    }

    @GetMapping("/pump/{pumpId}/dispenser/{dispenserCode}")
    public ResponseEntity<NozzleDTO> getNozzleByPumpAndDispenserCode(
            @PathVariable Long pumpId,
            @PathVariable String dispenserCode) {
        try {
            NozzleDTO nozzle = nozzleService.getNozzleByPumpAndDispenserCode(pumpId, dispenserCode);
            return new ResponseEntity<>(nozzle, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
