package com.nexusbank.admin.service;

import com.nexusbank.customer.dto.TransactionDTO;
import java.util.List;

public interface AdminTransactionService {
    List<TransactionDTO> getAllTransactions();
    List<TransactionDTO> getCustomerTransactions(Long customerId);
    List<TransactionDTO> getAccountTransactions(String accountNumber);
}