package com.nexusbank.admin.service;

import com.nexusbank.customer.dto.LoanDTO;
import java.util.List;

public interface AdminLoanService {
    List<LoanDTO> getAllLoans();
    List<LoanDTO> getPendingLoans();
    List<LoanDTO> getLoansByCustomerId(Long customerId);
    List<LoanDTO> getLoansByAccountNumber(String accountNumber);
    LoanDTO getLoanById(Long loanId);
    void approveLoan(Long loanId);
    void rejectLoan(Long loanId);
}
