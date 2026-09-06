package com.nexusbank.admin.feign;

import com.nexusbank.customer.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "customer-service", contextId = "customerFeignClient", fallback = com.nexusbank.admin.feign.fallback.CustomerFeignFallback.class)
public interface CustomerFeignClient {
    @GetMapping("/api/customers")
    List<CustomerDTO> getAllCustomers();

    @GetMapping("/api/customers/{customerId}")
    CustomerDTO getCustomerById(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/customers/account/{accountNumber}")
    CustomerDTO getCustomerByAccountNumber(@PathVariable("accountNumber") String accountNumber);

    @PutMapping("/api/customers/{customerId}/activate")
    void activateCustomer(@PathVariable("customerId") Long customerId);

    @PutMapping("/api/customers/{customerId}/deactivate")
    void deactivateCustomer(@PathVariable("customerId") Long customerId);

    @DeleteMapping("/api/customers/{customerId}")
    void deleteCustomer(@PathVariable("customerId") Long customerId);
}