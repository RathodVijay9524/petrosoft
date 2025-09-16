package com.vijay.petrosoft.controller;

import com.vijay.petrosoft.dto.SaleTransactionDTO;
import com.vijay.petrosoft.service.SaleTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleTransactionController {

    private final SaleTransactionService saleTransactionService;

    @PostMapping
    public ResponseEntity<SaleTransactionDTO> createSale(@RequestBody SaleTransactionDTO saleDTO) {
        try {
            SaleTransactionDTO createdSale = saleTransactionService.createSale(saleDTO);
            return new ResponseEntity<>(createdSale, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleTransactionDTO> getSaleById(@PathVariable Long id) {
        return saleTransactionService.getSaleById(id)
                .map(sale -> new ResponseEntity<>(sale, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<SaleTransactionDTO>> getAllSales() {
        List<SaleTransactionDTO> sales = saleTransactionService.getAllSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleTransactionDTO> updateSale(@PathVariable Long id, @RequestBody SaleTransactionDTO saleDTO) {
        try {
            SaleTransactionDTO updatedSale = saleTransactionService.updateSale(id, saleDTO);
            return new ResponseEntity<>(updatedSale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        try {
            saleTransactionService.deleteSale(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByPumpId(@PathVariable Long pumpId) {
        List<SaleTransactionDTO> sales = saleTransactionService.getSalesByPumpId(pumpId);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/shift/{shiftId}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByShiftId(@PathVariable Long shiftId) {
        List<SaleTransactionDTO> sales = saleTransactionService.getSalesByShiftId(shiftId);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SaleTransactionDTO> sales = saleTransactionService.getSalesByDateRange(startDate, endDate);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleTransactionDTO>> getSalesByCustomerId(@PathVariable Long customerId) {
        List<SaleTransactionDTO> sales = saleTransactionService.getSalesByCustomerId(customerId);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/credit")
    public ResponseEntity<List<SaleTransactionDTO>> getCreditSales() {
        List<SaleTransactionDTO> sales = saleTransactionService.getCreditSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/cash")
    public ResponseEntity<List<SaleTransactionDTO>> getCashSales() {
        List<SaleTransactionDTO> sales = saleTransactionService.getCashSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/total/date/{date}")
    public ResponseEntity<BigDecimal> getTotalSalesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BigDecimal total = saleTransactionService.getTotalSalesByDate(date);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @GetMapping("/total/shift/{shiftId}")
    public ResponseEntity<BigDecimal> getTotalSalesByShift(@PathVariable Long shiftId) {
        BigDecimal total = saleTransactionService.getTotalSalesByShift(shiftId);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @PostMapping("/process-fuel-sale")
    public ResponseEntity<SaleTransactionDTO> processFuelSale(
            @RequestParam Long nozzleId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long customerId,
            @RequestParam boolean isCredit) {
        try {
            SaleTransactionDTO sale = saleTransactionService.processFuelSale(nozzleId, quantity, customerId, isCredit);
            return new ResponseEntity<>(sale, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
