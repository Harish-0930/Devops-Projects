package com.nexusbank.customer.service.impl;

import com.nexusbank.customer.dto.LoanDTO;
import com.nexusbank.customer.entity.Account;
import com.nexusbank.customer.entity.Loan;
import com.nexusbank.customer.enums.AccountStatus;
import com.nexusbank.customer.enums.LoanStatus;
import com.nexusbank.customer.enums.LoanType;
import com.nexusbank.customer.exception.BadRequestException;
import com.nexusbank.customer.exception.ResourceNotFoundException;
import com.nexusbank.customer.repository.AccountRepository;
import com.nexusbank.customer.repository.LoanRepository;
import com.nexusbank.customer.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public LoanDTO applyLoan(Long customerId, LoanDTO dto) {
        Account account = accountRepository.findByAccountNumberAndCustomerCustomerId(dto.getAccountNumber(), customerId)
                .orElseThrow(() -> new BadRequestException("Select one of your own accounts for this loan"));
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Loans can only be applied for using an active account");
        }
        if (dto.getAmount() == null || dto.getAmount().signum() <= 0) {
            throw new BadRequestException("Loan amount must be positive");
        }

        Loan loan = Loan.builder()
                .loanType(dto.getLoanType())
                .amount(dto.getAmount())
                .tenure(dto.getTenure())
                // Rates are bank-defined and must never be supplied by a customer.
                .interestRate(defaultInterestRate(dto.getLoanType()))
                .loanStatus(LoanStatus.PENDING)
                .account(account)
                .build();

        return mapToDTO(loanRepository.save(loan));
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDTO getLoanById(Long loanId) {
        return mapToDTO(loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDTO> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByAccountCustomerCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDTO> getLoansByAccountNumber(String accountNumber) {
        return loanRepository.findByAccountAccountNumber(accountNumber).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDTO> getPendingLoans() {
        return loanRepository.findByLoanStatus(LoanStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getLoanStatus() != LoanStatus.PENDING) {
            throw new BadRequestException("Only pending loans can be approved");
        }
        loan.setLoanStatus(LoanStatus.APPROVED);
        loanRepository.save(loan);
    }

    @Override
    public void rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getLoanStatus() != LoanStatus.PENDING) {
            throw new BadRequestException("Only pending loans can be rejected");
        }
        loan.setLoanStatus(LoanStatus.REJECTED);
        loanRepository.save(loan);
    }

    @Override
    public void disburseLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getLoanStatus() != LoanStatus.APPROVED) {
            throw new BadRequestException("Only approved loans can be disbursed");
        }
        loan.setLoanStatus(LoanStatus.DISBURSED);
        loanRepository.save(loan);
    }

    private LoanDTO mapToDTO(Loan loan) {
        return LoanDTO.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType())
                .amount(loan.getAmount())
                .tenure(loan.getTenure())
                .interestRate(loan.getInterestRate())
                .appliedDate(loan.getAppliedDate())
                .loanStatus(loan.getLoanStatus())
                .accountNumber(loan.getAccount().getAccountNumber())
                .customerId(loan.getAccount().getCustomer().getCustomerId())
                .build();
    }

    private BigDecimal defaultInterestRate(LoanType loanType) {
        if (loanType == null) {
            throw new BadRequestException("Loan type is required");
        }
        return switch (loanType) {
            case VEHICLE -> new BigDecimal("8.00");
            case HOME -> new BigDecimal("10.00");
            case EDUCATION -> new BigDecimal("6.00");
            case BUSINESS -> new BigDecimal("12.00");
            case PERSONAL -> new BigDecimal("18.00");
        };
    }
}
