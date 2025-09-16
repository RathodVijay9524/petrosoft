package com.vijay.petrosoft.service.impl;

import com.vijay.petrosoft.domain.Customer;
import com.vijay.petrosoft.dto.CustomerDTO;
import com.vijay.petrosoft.repository.CustomerRepository;
import com.vijay.petrosoft.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getCode() != null && isCustomerCodeExists(customerDTO.getCode())) {
            throw new RuntimeException("Customer code already exists: " + customerDTO.getCode());
        }

        Customer customer = Customer.builder()
                .name(customerDTO.getName())
                .code(customerDTO.getCode())
                .phone(customerDTO.getPhone())
                .email(customerDTO.getEmail())
                .address(customerDTO.getAddress())
                .outstanding(customerDTO.getOutstanding() != null ? customerDTO.getOutstanding() : BigDecimal.ZERO)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setName(customerDTO.getName());
        customer.setCode(customerDTO.getCode());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setAddress(customerDTO.getAddress());
        customer.setOutstanding(customerDTO.getOutstanding());

        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersWithOutstanding() {
        return customerRepository.findByOutstandingGreaterThan(BigDecimal.ZERO).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO updateOutstanding(Long customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        customer.setOutstanding(amount);
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    public CustomerDTO addToOutstanding(Long customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        BigDecimal newOutstanding = customer.getOutstanding().add(amount);
        customer.setOutstanding(newOutstanding);
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    public CustomerDTO subtractFromOutstanding(Long customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        BigDecimal newOutstanding = customer.getOutstanding().subtract(amount);
        customer.setOutstanding(newOutstanding);
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCustomerCodeExists(String code) {
        return customerRepository.existsByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByCode(String code) {
        Customer customer = customerRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Customer not found with code: " + code));
        return convertToDTO(customer);
    }

    private CustomerDTO convertToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .code(customer.getCode())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .outstanding(customer.getOutstanding())
                .build();
    }
}
