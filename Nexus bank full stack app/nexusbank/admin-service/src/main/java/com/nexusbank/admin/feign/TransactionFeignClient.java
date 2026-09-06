package com.nexusbank.admin.feign;

import com.nexusbank.customer.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "customer-service", contextId = "transactionFeignClient", fallback = com.nexusbank.admin.feign.fallback.TransactionFeignFallback.class)
public interface TransactionFeignClient {
    @GetMapping("/api/transactions")
    List<TransactionDTO> getAllTransactions();

    @GetMapping("/api/transactions/customer/{customerId}")
    List<TransactionDTO> getCustomerTransactions(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/transactions/admin/account/{accountNumber}")
    List<TransactionDTO> getAccountTransactions(@PathVariable("accountNumber") String accountNumber);
}
