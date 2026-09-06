package com.nexusbank.customer.service;

import com.nexusbank.customer.dto.LoanDTO;
import java.util.List;

public interface LoanService {
    LoanDTO applyLoan(Long customerId, LoanDTO loanDTO);
    LoanDTO getLoanById(Long loanId);
    List<LoanDTO> getLoansByCustomerId(Long customerId);
    List<LoanDTO> getLoansByAccountNumber(String accountNumber);
    List<LoanDTO> getAllLoans();
    List<LoanDTO> getPendingLoans();
    void approveLoan(Long loanId);
    void rejectLoan(Long loanId);
    void disburseLoan(Long loanId);
}
