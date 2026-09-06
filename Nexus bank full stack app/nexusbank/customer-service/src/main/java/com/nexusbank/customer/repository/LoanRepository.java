package com.nexusbank.customer.repository;

import com.nexusbank.customer.entity.Loan;
import com.nexusbank.customer.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByAccountCustomerCustomerId(Long customerId);
    List<Loan> findByAccountAccountNumber(String accountNumber);
    List<Loan> findByLoanStatus(LoanStatus status);
    long countByLoanStatus(LoanStatus status);
    long countByAccountCustomerCustomerId(Long customerId);
}
