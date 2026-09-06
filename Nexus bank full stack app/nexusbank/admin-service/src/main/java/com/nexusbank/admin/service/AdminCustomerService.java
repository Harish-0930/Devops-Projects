package com.nexusbank.admin.service;

import com.nexusbank.customer.dto.CustomerDTO;
import java.util.List;

public interface AdminCustomerService {
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(Long customerId);
    CustomerDTO getCustomerByAccountNumber(String accountNumber);
    void activateCustomer(Long customerId);
    void deactivateCustomer(Long customerId);
    void deleteCustomer(Long customerId);
}