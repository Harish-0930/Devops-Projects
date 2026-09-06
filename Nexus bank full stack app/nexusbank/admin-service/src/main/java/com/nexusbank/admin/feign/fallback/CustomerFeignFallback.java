package com.nexusbank.admin.feign.fallback;

import com.nexusbank.customer.dto.CustomerDTO;
import com.nexusbank.admin.feign.CustomerFeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CustomerFeignFallback implements CustomerFeignClient {
    @Override public List<CustomerDTO> getAllCustomers() { return Collections.emptyList(); }
    @Override public CustomerDTO getCustomerById(Long id) { return null; }
    @Override public CustomerDTO getCustomerByAccountNumber(String acc) { return null; }
    @Override public void activateCustomer(Long id) {}
    @Override public void deactivateCustomer(Long id) {}
    @Override public void deleteCustomer(Long id) {}
}