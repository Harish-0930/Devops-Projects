package com.nexusbank.admin.service.impl;

import com.nexusbank.admin.dto.AdminDashboardDTO;
import com.nexusbank.admin.feign.CustomerFeignClient;
import com.nexusbank.admin.feign.AccountFeignClient;
import com.nexusbank.admin.feign.LoanFeignClient;
import com.nexusbank.admin.feign.TransactionFeignClient;
import com.nexusbank.admin.service.AdminDashboardService;
import com.nexusbank.customer.dto.AccountDTO;
import com.nexusbank.customer.dto.LoanDTO;
import com.nexusbank.customer.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired private CustomerFeignClient customerFeignClient;
    @Autowired private AccountFeignClient accountFeignClient;
    @Autowired private LoanFeignClient loanFeignClient;
    @Autowired private TransactionFeignClient transactionFeignClient;

    @Override
    public AdminDashboardDTO getDashboard() {
        List<CustomerDTO> customers = safeList(() -> customerFeignClient.getAllCustomers());
        List<AccountDTO> accounts = safeList(() -> accountFeignClient.getAllAccounts());
        List<LoanDTO> loans = safeList(() -> loanFeignClient.getAllLoans());
        List<com.nexusbank.customer.dto.TransactionDTO> txns = safeList(() -> transactionFeignClient.getAllTransactions());

        long pendingAccounts = accounts.stream().filter(a -> a.getAccountStatus() != null && "PENDING".equals(a.getAccountStatus().name())).count();
        long pendingLoans = loans.stream().filter(l -> l.getLoanStatus() != null && "PENDING".equals(l.getLoanStatus().name())).count();

        BigDecimal totalDeposits = txns.stream()
                .filter(t -> t.getTransactionType() != null && "DEPOSIT".equals(t.getTransactionType().name()))
                .map(com.nexusbank.customer.dto.TransactionDTO::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = txns.stream()
                .filter(t -> t.getTransactionType() != null && "WITHDRAWAL".equals(t.getTransactionType().name()))
                .map(com.nexusbank.customer.dto.TransactionDTO::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminDashboardDTO.builder()
                .totalCustomers((long) customers.size())
                .totalAccounts((long) accounts.size())
                .totalTransactions((long) txns.size())
                .totalLoans((long) loans.size())
                .pendingAccountApprovals(pendingAccounts)
                .pendingLoanApprovals(pendingLoans)
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .build();
    }

    private <T> List<T> safeList(ThrowingSupplier<List<T>> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            return List.of();
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}