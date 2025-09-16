package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Vendor;
import com.vijay.petrosoft.dto.VendorDTO;
import java.util.List;
import java.util.Optional;

public interface VendorService {
    VendorDTO createVendor(VendorDTO vendorDTO);
    VendorDTO updateVendor(Long id, VendorDTO vendorDTO);
    Optional<VendorDTO> getVendorById(Long id);
    List<VendorDTO> getAllVendors();
    void deleteVendor(Long id);
    List<VendorDTO> getVendorsByPumpId(Long pumpId);
    boolean isVendorNameExists(String name);
    VendorDTO getVendorByName(String name);
}
