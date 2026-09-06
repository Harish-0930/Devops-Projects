package com.nexusbank.admin.feign.fallback;

import com.nexusbank.customer.dto.AccountDTO;
import com.nexusbank.admin.feign.AccountFeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AccountFeignFallback implements AccountFeignClient {
    @Override public List<AccountDTO> getAllAccounts() { return Collections.emptyList(); }
    @Override public AccountDTO getAccount(String num) { return null; }
    @Override public List<AccountDTO> getAccountsByCustomerId(Long customerId) { return Collections.emptyList(); }
    @Override public void approveAccount(String num) {}
    @Override public void rejectAccount(String num) {}
    @Override public void freezeAccount(String num) {}
    @Override public void unfreezeAccount(String num) {}
}
