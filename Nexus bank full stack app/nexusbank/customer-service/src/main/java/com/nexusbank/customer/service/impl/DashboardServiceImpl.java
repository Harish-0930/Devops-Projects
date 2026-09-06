package com.nexusbank.customer.service.impl;

import com.nexusbank.customer.dto.*;
import com.nexusbank.customer.entity.Account;
import com.nexusbank.customer.entity.Customer;
import com.nexusbank.customer.exception.ResourceNotFoundException;
import com.nexusbank.customer.repository.CustomerRepository;
import com.nexusbank.customer.repository.TransactionRepository;
import com.nexusbank.customer.repository.LoanRepository;
import com.nexusbank.customer.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private LoanRepository loanRepository;

    @Override
    public DashboardDTO getCustomerDashboard(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<AccountDTO> accounts = customer.getAccounts() != null ? 
                customer.getAccounts().stream().map(a -> AccountDTO.builder()
                        .accountNumber(a.getAccountNumber())
                        .accountType(a.getAccountType())
                        .balance(a.getBalance())
                        .accountStatus(a.getAccountStatus())
                        .createdDate(a.getCreatedDate())
                        .customerId(customerId)
                        .build()).collect(Collectors.toList()) : List.of();

        BigDecimal totalBalance = accounts.stream()
                .map(AccountDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TransactionDTO> recentTxns = transactionRepository.findByCustomerId(customerId).stream()
                .limit(5).map(t -> TransactionDTO.builder()
                        .transactionId(t.getTransactionId())
                        .fromAccountNumber(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                        .toAccountNumber(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                        .amount(t.getAmount())
                        .transactionType(t.getTransactionType())
                        .transactionDate(t.getTransactionDate())
                        .remarks(t.getRemarks())
                        .balanceAfter(t.getBalanceAfter())
                        .build()).collect(Collectors.toList());

        List<LoanDTO> loans = loanRepository.findByAccountCustomerCustomerId(customerId).stream().map(l -> LoanDTO.builder()
                        .loanId(l.getLoanId())
                        .accountNumber(l.getAccount().getAccountNumber())
                        .loanType(l.getLoanType())
                        .amount(l.getAmount())
                        .tenure(l.getTenure())
                        .interestRate(l.getInterestRate())
                        .appliedDate(l.getAppliedDate())
                        .loanStatus(l.getLoanStatus())
                        .customerId(customerId)
                        .build()).collect(Collectors.toList());

        return DashboardDTO.builder()
                .customer(CustomerDTO.builder()
                        .customerId(customer.getCustomerId())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .email(customer.getEmail())
                        .phoneNumber(customer.getPhoneNumber())
                        .username(customer.getUsername())
                        .status(customer.getStatus())
                        .createdDate(customer.getCreatedDate())
                        .build())
                .accounts(accounts)
                .totalBalance(totalBalance)
                .recentTransactions(recentTxns)
                .activeLoans(loans.stream().filter(l -> l.getLoanStatus().name().equals("PENDING") || 
                        l.getLoanStatus().name().equals("APPROVED") || l.getLoanStatus().name().equals("DISBURSED"))
                        .collect(Collectors.toList()))
                .totalAccounts((long) accounts.size())
                .totalTransactions(transactionRepository.countByFromAccountCustomerCustomerIdOrToAccountCustomerCustomerId(customerId, customerId))
                .totalLoans(loanRepository.countByAccountCustomerCustomerId(customerId))
                .build();
    }
}
