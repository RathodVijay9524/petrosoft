package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Pump;
import com.vijay.petrosoft.dto.PumpDTO;
import com.vijay.petrosoft.repository.PumpRepository;
import com.vijay.petrosoft.service.PumpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PumpServiceImpl implements PumpService {

    private final PumpRepository pumpRepository;

    @Override
    public PumpDTO createPump(PumpDTO pumpDTO) {
        if (isGstNumberExists(pumpDTO.getGstNumber())) {
            throw new RuntimeException("GST Number already exists: " + pumpDTO.getGstNumber());
        }

        Pump pump = Pump.builder()
                .name(pumpDTO.getName())
                .code(pumpDTO.getCode())
                .address(pumpDTO.getAddress())
                .city(pumpDTO.getCity())
                .state(pumpDTO.getState())
                .gstNumber(pumpDTO.getGstNumber())
                .contactPhone(pumpDTO.getContactPhone())
                .build();

        Pump savedPump = pumpRepository.save(pump);
        return convertToDTO(savedPump);
    }

    @Override
    public PumpDTO updatePump(Long id, PumpDTO pumpDTO) {
        Pump pump = pumpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pump not found with id: " + id));

        pump.setName(pumpDTO.getName());
        pump.setCode(pumpDTO.getCode());
        pump.setAddress(pumpDTO.getAddress());
        pump.setCity(pumpDTO.getCity());
        pump.setState(pumpDTO.getState());
        pump.setGstNumber(pumpDTO.getGstNumber());
        pump.setContactPhone(pumpDTO.getContactPhone());

        Pump updatedPump = pumpRepository.save(pump);
        return convertToDTO(updatedPump);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PumpDTO> getPumpById(Long id) {
        return pumpRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PumpDTO> getAllPumps() {
        return pumpRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePump(Long id) {
        if (!pumpRepository.existsById(id)) {
            throw new RuntimeException("Pump not found with id: " + id);
        }
        pumpRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PumpDTO> getPumpsByCity(String city) {
        return pumpRepository.findByCity(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PumpDTO> getPumpsByState(String state) {
        return pumpRepository.findByState(state).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isGstNumberExists(String gstNumber) {
        return pumpRepository.existsByGstNumber(gstNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public PumpDTO getPumpByGstNumber(String gstNumber) {
        Pump pump = pumpRepository.findByGstNumber(gstNumber)
                .orElseThrow(() -> new RuntimeException("Pump not found with GST Number: " + gstNumber));
        return convertToDTO(pump);
    }

    private PumpDTO convertToDTO(Pump pump) {
        return PumpDTO.builder()
                .id(pump.getId())
                .name(pump.getName())
                .code(pump.getCode())
                .address(pump.getAddress())
                .city(pump.getCity())
                .state(pump.getState())
                .gstNumber(pump.getGstNumber())
                .contactPhone(pump.getContactPhone())
                .build();
    }
}
