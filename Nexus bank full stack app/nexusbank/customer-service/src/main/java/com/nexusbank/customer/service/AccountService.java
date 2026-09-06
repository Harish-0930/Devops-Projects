package com.nexusbank.customer.service;

import com.nexusbank.customer.dto.AccountDTO;
import java.util.List;

public interface AccountService {
    AccountDTO createAccount(Long customerId, AccountDTO accountDTO);
    AccountDTO getAccountByNumber(String accountNumber);
    List<AccountDTO> getAccountsByCustomerId(Long customerId);
    List<AccountDTO> getAllAccounts();
    void approveAccount(String accountNumber);
    void rejectAccount(String accountNumber);
    void freezeAccount(String accountNumber);
    void unfreezeAccount(String accountNumber);
    void closeAccount(String accountNumber);
}