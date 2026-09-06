package com.nexusbank.admin.service;

import com.nexusbank.customer.dto.AccountDTO;
import java.util.List;

public interface AdminAccountService {
    List<AccountDTO> getAllAccounts();
    AccountDTO getAccount(String accountNumber);
    List<AccountDTO> getAccountsByCustomerId(Long customerId);
    void approveAccount(String accountNumber);
    void rejectAccount(String accountNumber);
    void freezeAccount(String accountNumber);
    void unfreezeAccount(String accountNumber);
}
