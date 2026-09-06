package com.nexusbank.customer.service;

import com.nexusbank.customer.dto.*;
import java.util.List;

public interface CustomerService {
    CustomerDTO registerCustomer(CustomerDTO customerDTO, String password);
    LoginResponse login(LoginRequest loginRequest);
    CustomerDTO getCustomerById(Long customerId);
    CustomerDTO getCustomerByUsername(String username);
    CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO);
    void updatePassword(Long customerId, String oldPassword, String newPassword);
    List<CustomerDTO> getAllCustomers();
    void activateCustomer(Long customerId);
    void deactivateCustomer(Long customerId);
    void deleteCustomer(Long customerId);
    AddressDTO addAddress(Long customerId, AddressDTO addressDTO);
    AddressDTO updateAddress(Long customerId, AddressDTO addressDTO);
    AddressDTO getAddress(Long customerId);
    CustomerDTO getCustomerByAccountNumber(String accountNumber);
}