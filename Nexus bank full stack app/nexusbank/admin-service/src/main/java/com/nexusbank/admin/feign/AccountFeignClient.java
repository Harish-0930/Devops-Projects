package com.nexusbank.admin.feign;

import com.nexusbank.customer.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "customer-service", contextId = "accountFeignClient", fallback = com.nexusbank.admin.feign.fallback.AccountFeignFallback.class)
public interface AccountFeignClient {
    @GetMapping("/api/accounts")
    List<AccountDTO> getAllAccounts();

    @GetMapping("/api/accounts/{accountNumber}")
    AccountDTO getAccount(@PathVariable("accountNumber") String accountNumber);

    @GetMapping("/api/accounts/customer/{customerId}")
    List<AccountDTO> getAccountsByCustomerId(@PathVariable("customerId") Long customerId);

    @PutMapping("/api/accounts/{accountNumber}/approve")
    void approveAccount(@PathVariable("accountNumber") String accountNumber);

    @PutMapping("/api/accounts/{accountNumber}/reject")
    void rejectAccount(@PathVariable("accountNumber") String accountNumber);

    @PutMapping("/api/accounts/{accountNumber}/freeze")
    void freezeAccount(@PathVariable("accountNumber") String accountNumber);

    @PutMapping("/api/accounts/{accountNumber}/unfreeze")
    void unfreezeAccount(@PathVariable("accountNumber") String accountNumber);
}
