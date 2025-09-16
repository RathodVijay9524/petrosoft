package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Vendor;
import com.vijay.petrosoft.dto.VendorDTO;
import com.vijay.petrosoft.repository.VendorRepository;
import com.vijay.petrosoft.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    public VendorDTO createVendor(VendorDTO vendorDTO) {
        if (isVendorNameExists(vendorDTO.getName())) {
            throw new RuntimeException("Vendor name already exists: " + vendorDTO.getName());
        }

        Vendor vendor = Vendor.builder()
                .name(vendorDTO.getName())
                .phone(vendorDTO.getPhone())
                .email(vendorDTO.getEmail())
                .address(vendorDTO.getAddress())
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);
        return convertToDTO(savedVendor);
    }

    @Override
    public VendorDTO updateVendor(Long id, VendorDTO vendorDTO) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        vendor.setName(vendorDTO.getName());
        vendor.setPhone(vendorDTO.getPhone());
        vendor.setEmail(vendorDTO.getEmail());
        vendor.setAddress(vendorDTO.getAddress());

        Vendor updatedVendor = vendorRepository.save(vendor);
        return convertToDTO(updatedVendor);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VendorDTO> getVendorById(Long id) {
        return vendorRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorDTO> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVendor(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new RuntimeException("Vendor not found with id: " + id);
        }
        vendorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorDTO> getVendorsByPumpId(Long pumpId) {
        // This would require a pumpId field in Vendor entity or a separate mapping table
        // For now, returning all vendors
        return getAllVendors();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVendorNameExists(String name) {
        return vendorRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorDTO getVendorByName(String name) {
        Vendor vendor = vendorRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Vendor not found with name: " + name));
        return convertToDTO(vendor);
    }

    private VendorDTO convertToDTO(Vendor vendor) {
        return VendorDTO.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .phone(vendor.getPhone())
                .email(vendor.getEmail())
                .address(vendor.getAddress())
                .build();
    }
}
