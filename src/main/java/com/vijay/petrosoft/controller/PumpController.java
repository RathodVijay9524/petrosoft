package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.PumpDTO;
import com.vijay.petrosoft.service.PumpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pumps")
@RequiredArgsConstructor
public class PumpController {

    private final PumpService pumpService;

    @PostMapping
    public ResponseEntity<PumpDTO> createPump(@RequestBody PumpDTO pumpDTO) {
        try {
            PumpDTO createdPump = pumpService.createPump(pumpDTO);
            return new ResponseEntity<>(createdPump, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PumpDTO> getPumpById(@PathVariable Long id) {
        return pumpService.getPumpById(id)
                .map(pump -> new ResponseEntity<>(pump, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<PumpDTO>> getAllPumps() {
        List<PumpDTO> pumps = pumpService.getAllPumps();
        return new ResponseEntity<>(pumps, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PumpDTO> updatePump(@PathVariable Long id, @RequestBody PumpDTO pumpDTO) {
        try {
            PumpDTO updatedPump = pumpService.updatePump(id, pumpDTO);
            return new ResponseEntity<>(updatedPump, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePump(@PathVariable Long id) {
        try {
            pumpService.deletePump(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<PumpDTO>> getPumpsByCity(@PathVariable String city) {
        List<PumpDTO> pumps = pumpService.getPumpsByCity(city);
        return new ResponseEntity<>(pumps, HttpStatus.OK);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<PumpDTO>> getPumpsByState(@PathVariable String state) {
        List<PumpDTO> pumps = pumpService.getPumpsByState(state);
        return new ResponseEntity<>(pumps, HttpStatus.OK);
    }

    @GetMapping("/gst/{gstNumber}")
    public ResponseEntity<PumpDTO> getPumpByGstNumber(@PathVariable String gstNumber) {
        try {
            PumpDTO pump = pumpService.getPumpByGstNumber(gstNumber);
            return new ResponseEntity<>(pump, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
