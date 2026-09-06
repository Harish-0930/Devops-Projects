package com.nexusbank.admin.feign;

import com.nexusbank.customer.dto.LoanDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "customer-service", contextId = "loanFeignClient", fallback = com.nexusbank.admin.feign.fallback.LoanFeignFallback.class)
public interface LoanFeignClient {
    @GetMapping("/api/loans")
    List<LoanDTO> getAllLoans();

    @GetMapping("/api/loans/pending")
    List<LoanDTO> getPendingLoans();

    @GetMapping("/api/loans/customer/{customerId}")
    List<LoanDTO> getLoansByCustomerId(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/loans/account/{accountNumber}")
    List<LoanDTO> getLoansByAccountNumber(@PathVariable("accountNumber") String accountNumber);

    @GetMapping("/api/loans/status/{loanId}")
    LoanDTO getLoanById(@PathVariable("loanId") Long loanId);

    @PutMapping("/api/loans/{loanId}/approve")
    void approveLoan(@PathVariable("loanId") Long loanId);

    @PutMapping("/api/loans/{loanId}/reject")
    void rejectLoan(@PathVariable("loanId") Long loanId);
}
