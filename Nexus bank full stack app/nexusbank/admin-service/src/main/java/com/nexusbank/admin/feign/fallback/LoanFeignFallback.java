package com.nexusbank.admin.feign.fallback;

import com.nexusbank.customer.dto.LoanDTO;
import com.nexusbank.admin.feign.LoanFeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LoanFeignFallback implements LoanFeignClient {
    @Override public List<LoanDTO> getAllLoans() { return Collections.emptyList(); }
    @Override public List<LoanDTO> getPendingLoans() { return Collections.emptyList(); }
    @Override public List<LoanDTO> getLoansByCustomerId(Long customerId) { return Collections.emptyList(); }
    @Override public List<LoanDTO> getLoansByAccountNumber(String accountNumber) { return Collections.emptyList(); }
    @Override public LoanDTO getLoanById(Long id) { return null; }
    @Override public void approveLoan(Long id) {}
    @Override public void rejectLoan(Long id) {}
}
