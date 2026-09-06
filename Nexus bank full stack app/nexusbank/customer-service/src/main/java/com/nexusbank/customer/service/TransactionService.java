package com.nexusbank.customer.service;

import com.nexusbank.customer.dto.*;
import java.util.List;

public interface TransactionService {
    TransactionDTO deposit(DepositRequest request);
    TransactionDTO withdraw(WithdrawRequest request);
    TransactionDTO transfer(TransferRequest request);
    List<TransactionDTO> getTransactionHistory(String accountNumber);
    List<TransactionDTO> getMiniStatement(String accountNumber);
    List<TransactionDTO> getAllTransactions();
    List<TransactionDTO> getTransactionsByCustomerId(Long customerId);
}