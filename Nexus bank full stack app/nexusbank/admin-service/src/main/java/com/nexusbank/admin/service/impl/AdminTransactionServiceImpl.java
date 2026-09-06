package com.nexusbank.admin.service.impl;

import com.nexusbank.admin.feign.TransactionFeignClient;
import com.nexusbank.admin.service.AdminTransactionService;
import com.nexusbank.customer.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminTransactionServiceImpl implements AdminTransactionService {

    @Autowired private TransactionFeignClient transactionFeignClient;

    @Override public List<TransactionDTO> getAllTransactions() { return safeList(() -> transactionFeignClient.getAllTransactions()); }
    @Override public List<TransactionDTO> getCustomerTransactions(Long id) { return safeList(() -> transactionFeignClient.getCustomerTransactions(id)); }
    @Override
    public List<TransactionDTO> getAccountTransactions(String accountNumber) {
        // The general admin feed is already authenticated between services.
        // Filter its ledger entries here, avoiding a second Feign route that
        // can be hidden by the circuit-breaker fallback as an empty response.
        return safeList(() -> transactionFeignClient.getAllTransactions()).stream()
                .filter(transaction -> accountNumber.equals(transaction.getFromAccountNumber())
                        || accountNumber.equals(transaction.getToAccountNumber()))
                .filter(transaction -> ("DEBIT".equals(transaction.getTransactionType().name())
                        && accountNumber.equals(transaction.getFromAccountNumber()))
                        || ("CREDIT".equals(transaction.getTransactionType().name())
                        && accountNumber.equals(transaction.getToAccountNumber())))
                .toList();
    }

    private <T> List<T> safeList(ThrowingSupplier<List<T>> supplier) { try { return supplier.get(); } catch (Exception ex) { return List.of(); } }

    @FunctionalInterface private interface ThrowingSupplier<T> { T get() throws Exception; }
}
