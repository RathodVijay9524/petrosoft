package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.service.SampleDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sample-data")
@RequiredArgsConstructor
@Slf4j
@Profile("startup")
@Tag(name = "Sample Data", description = "Sample data management endpoints")
public class SampleDataController {

    private final SampleDataService sampleDataService;

    @PostMapping("/create")
    @Operation(summary = "Create sample data", description = "Manually trigger sample data creation")
    public ResponseEntity<Map<String, Object>> createSampleData() {
        try {
            log.info("Manual sample data creation triggered");
            sampleDataService.run(new String[0]);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sample data created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating sample data: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating sample data: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Get sample data status", description = "Get information about existing sample data")
    public ResponseEntity<Map<String, Object>> getSampleDataStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("message", "Sample data status endpoint");
            status.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting sample data status: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error getting sample data status: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}