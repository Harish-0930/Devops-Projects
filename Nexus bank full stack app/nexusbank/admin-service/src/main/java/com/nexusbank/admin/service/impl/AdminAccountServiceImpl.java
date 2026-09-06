package com.nexusbank.admin.service.impl;

import com.nexusbank.admin.feign.AccountFeignClient;
import com.nexusbank.admin.service.AdminAccountService;
import com.nexusbank.customer.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAccountServiceImpl implements AdminAccountService {

    @Autowired private AccountFeignClient accountFeignClient;

    @Override public List<AccountDTO> getAllAccounts() { return safeList(() -> accountFeignClient.getAllAccounts()); }
    @Override public AccountDTO getAccount(String num) { return safeGet(() -> accountFeignClient.getAccount(num)); }
    @Override public List<AccountDTO> getAccountsByCustomerId(Long id) { return safeList(() -> accountFeignClient.getAccountsByCustomerId(id)); }
    @Override public void approveAccount(String num) { safeRun(() -> accountFeignClient.approveAccount(num)); }
    @Override public void rejectAccount(String num) { safeRun(() -> accountFeignClient.rejectAccount(num)); }
    @Override public void freezeAccount(String num) { safeRun(() -> accountFeignClient.freezeAccount(num)); }
    @Override public void unfreezeAccount(String num) { safeRun(() -> accountFeignClient.unfreezeAccount(num)); }

    private <T> List<T> safeList(ThrowingSupplier<List<T>> supplier) { try { return supplier.get(); } catch (Exception ex) { return List.of(); } }
    private <T> T safeGet(ThrowingSupplier<T> supplier) { try { return supplier.get(); } catch (Exception ex) { return null; } }
    private void safeRun(ThrowingRunnable runnable) { try { runnable.run(); } catch (Exception ignored) {} }

    @FunctionalInterface private interface ThrowingSupplier<T> { T get() throws Exception; }
    @FunctionalInterface private interface ThrowingRunnable { void run() throws Exception; }
}
