package com.vijay.petrosoft.service;

import com.vijay.petrosoft.domain.Customer;
import com.vijay.petrosoft.dto.CustomerDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    Optional<CustomerDTO> getCustomerById(Long id);
    List<CustomerDTO> getAllCustomers();
    void deleteCustomer(Long id);
    List<CustomerDTO> getCustomersWithOutstanding();
    CustomerDTO updateOutstanding(Long customerId, BigDecimal amount);
    CustomerDTO addToOutstanding(Long customerId, BigDecimal amount);
    CustomerDTO subtractFromOutstanding(Long customerId, BigDecimal amount);
    boolean isCustomerCodeExists(String code);
    CustomerDTO getCustomerByCode(String code);
}
