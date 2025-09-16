package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Employee;
import com.vijay.petrosoft.dto.EmployeeDTO;
import com.vijay.petrosoft.repository.EmployeeRepository;
import com.vijay.petrosoft.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO.getEmployeeCode() != null && isEmployeeCodeExists(employeeDTO.getEmployeeCode())) {
            throw new RuntimeException("Employee code already exists: " + employeeDTO.getEmployeeCode());
        }

        Employee employee = Employee.builder()
                .employeeCode(employeeDTO.getEmployeeCode())
                .name(employeeDTO.getName())
                .role(employeeDTO.getRole())
                .phone(employeeDTO.getPhone())
                .email(employeeDTO.getEmail())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setEmployeeCode(employeeDTO.getEmployeeCode());
        employee.setName(employeeDTO.getName());
        employee.setRole(employeeDTO.getRole());
        employee.setPhone(employeeDTO.getPhone());
        employee.setEmail(employeeDTO.getEmail());

        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByRole(String role) {
        return employeeRepository.findByRole(role).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmployeeCodeExists(String employeeCode) {
        return employeeRepository.existsByEmployeeCode(employeeCode);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByCode(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee not found with code: " + employeeCode));
        return convertToDTO(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByPumpId(Long pumpId) {
        // This would require a pumpId field in Employee entity or a separate mapping table
        // For now, returning all employees
        return getAllEmployees();
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .name(employee.getName())
                .role(employee.getRole())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .build();
    }
}
