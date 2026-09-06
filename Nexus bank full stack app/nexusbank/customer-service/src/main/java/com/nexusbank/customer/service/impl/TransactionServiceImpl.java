package com.nexusbank.customer.service.impl;

import com.nexusbank.customer.dto.*;
import com.nexusbank.customer.entity.Account;
import com.nexusbank.customer.entity.Transaction;
import com.nexusbank.customer.enums.AccountStatus;
import com.nexusbank.customer.enums.TransactionType;
import com.nexusbank.customer.exception.BadRequestException;
import com.nexusbank.customer.exception.InsufficientBalanceException;
import com.nexusbank.customer.exception.ResourceNotFoundException;
import com.nexusbank.customer.repository.AccountRepository;
import com.nexusbank.customer.repository.TransactionRepository;
import com.nexusbank.customer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public TransactionDTO deposit(DepositRequest request) {
        requirePositiveAmount(request.getAmount());
        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .toAccount(account)
                .balanceAfter(account.getBalance())
                .amount(request.getAmount())
                .transactionType(TransactionType.CREDIT)
                .remarks(request.getRemarks())
                .build();

        return mapToDTO(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDTO withdraw(WithdrawRequest request) {
        requirePositiveAmount(request.getAmount());
        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .fromAccount(account)
                .balanceAfter(account.getBalance())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEBIT)
                .remarks(request.getRemarks())
                .build();

        return mapToDTO(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDTO transfer(TransferRequest request) {
        requirePositiveAmount(request.getAmount());
        Account source = accountRepository.findById(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
        Account dest = accountRepository.findById(request.getDestinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if (source.getAccountStatus() != AccountStatus.ACTIVE || dest.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("One or both accounts are not active");
        }

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source account");
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        dest.setBalance(dest.getBalance().add(request.getAmount()));

        accountRepository.save(source);
        accountRepository.save(dest);

        Transaction debitTransaction = Transaction.builder()
                .fromAccount(source)
                .toAccount(dest)
                .balanceAfter(source.getBalance())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEBIT)
                .remarks(request.getRemarks())
                .build();

        Transaction creditTransaction = Transaction.builder()
                .fromAccount(source)
                .toAccount(dest)
                .balanceAfter(dest.getBalance())
                .amount(request.getAmount())
                .transactionType(TransactionType.CREDIT)
                .remarks(request.getRemarks())
                .build();

        transactionRepository.save(creditTransaction);
        return mapToDTO(transactionRepository.save(debitTransaction));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionHistory(String accountNumber) {
        if (!accountRepository.existsById(accountNumber)) {
            throw new ResourceNotFoundException("Account not found");
        }
        return transactionRepository.findByAccountNumber(accountNumber).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getMiniStatement(String accountNumber) {
        if (!accountRepository.existsById(accountNumber)) {
            throw new ResourceNotFoundException("Account not found");
        }
        return transactionRepository.findMiniStatementByAccountNumber(accountNumber).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TransactionDTO mapToDTO(Transaction t) {
        return TransactionDTO.builder()
                .transactionId(t.getTransactionId())
                .fromAccountNumber(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                .amount(t.getAmount())
                .transactionType(t.getTransactionType())
                .transactionDate(t.getTransactionDate())
                .remarks(t.getRemarks())
                .balanceAfter(t.getBalanceAfter())
                .build();
    }

    private void requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
    }
}
