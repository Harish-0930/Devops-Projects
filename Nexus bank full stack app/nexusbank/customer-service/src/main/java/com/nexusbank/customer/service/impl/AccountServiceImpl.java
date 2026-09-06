package com.nexusbank.customer.service.impl;

import com.nexusbank.customer.dto.AccountDTO;
import com.nexusbank.customer.entity.Account;
import com.nexusbank.customer.entity.Customer;
import com.nexusbank.customer.enums.AccountStatus;
import com.nexusbank.customer.enums.AccountType;
import com.nexusbank.customer.exception.BadRequestException;
import com.nexusbank.customer.exception.ResourceNotFoundException;
import com.nexusbank.customer.repository.AccountRepository;
import com.nexusbank.customer.repository.CustomerRepository;
import com.nexusbank.customer.service.AccountService;
import com.nexusbank.customer.util.AccountNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public AccountDTO createAccount(Long customerId, AccountDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (dto.getBalance() != null && dto.getBalance().signum() < 0) {
            throw new BadRequestException("Initial balance cannot be negative");
        }

        Account account = Account.builder()
                .accountNumber(AccountNumberGenerator.generateAccountNumber())
                .accountType(dto.getAccountType())
                .balance(dto.getBalance() != null ? dto.getBalance() : java.math.BigDecimal.ZERO)
                .accountStatus(AccountStatus.PENDING)
                .customer(customer)
                .build();

        Account saved = accountRepository.save(account);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        return mapToDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void approveAccount(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Override
    public void rejectAccount(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setAccountStatus(AccountStatus.REJECTED);
        accountRepository.save(account);
    }

    @Override
    public void freezeAccount(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Only active accounts can be frozen");
        }
        account.setAccountStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
    }

    @Override
    public void unfreezeAccount(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getAccountStatus() != AccountStatus.FROZEN) {
            throw new BadRequestException("Only frozen accounts can be unfrozen");
        }
        account.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Override
    public void closeAccount(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getBalance().compareTo(java.math.BigDecimal.ZERO) != 0) {
            throw new BadRequestException("Account balance must be zero to close");
        }
        account.setAccountStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .accountStatus(account.getAccountStatus())
                .createdDate(account.getCreatedDate())
                .customerId(account.getCustomer().getCustomerId())
                .build();
    }
}
