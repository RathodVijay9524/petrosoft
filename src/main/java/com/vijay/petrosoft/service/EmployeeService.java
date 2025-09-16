package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Employee;
import com.vijay.petrosoft.dto.EmployeeDTO;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    Optional<EmployeeDTO> getEmployeeById(Long id);
    List<EmployeeDTO> getAllEmployees();
    void deleteEmployee(Long id);
    List<EmployeeDTO> getEmployeesByRole(String role);
    boolean isEmployeeCodeExists(String employeeCode);
    EmployeeDTO getEmployeeByCode(String employeeCode);
    List<EmployeeDTO> getEmployeesByPumpId(Long pumpId);
}
