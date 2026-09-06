package com.nexusbank.admin.service.impl;

import com.nexusbank.admin.feign.LoanFeignClient;
import com.nexusbank.admin.service.AdminLoanService;
import com.nexusbank.customer.dto.LoanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminLoanServiceImpl implements AdminLoanService {

    @Autowired private LoanFeignClient loanFeignClient;

    @Override public List<LoanDTO> getAllLoans() { return safeList(() -> loanFeignClient.getAllLoans()); }
    @Override public List<LoanDTO> getPendingLoans() { return safeList(() -> loanFeignClient.getPendingLoans()); }
    @Override public List<LoanDTO> getLoansByCustomerId(Long id) { return safeList(() -> loanFeignClient.getLoansByCustomerId(id)); }
    @Override public List<LoanDTO> getLoansByAccountNumber(String accountNumber) { return safeList(() -> loanFeignClient.getLoansByAccountNumber(accountNumber)); }
    @Override public LoanDTO getLoanById(Long id) { return safeGet(() -> loanFeignClient.getLoanById(id)); }
    @Override public void approveLoan(Long id) { safeRun(() -> loanFeignClient.approveLoan(id)); }
    @Override public void rejectLoan(Long id) { safeRun(() -> loanFeignClient.rejectLoan(id)); }

    private <T> List<T> safeList(ThrowingSupplier<List<T>> supplier) { try { return supplier.get(); } catch (Exception ex) { return List.of(); } }
    private <T> T safeGet(ThrowingSupplier<T> supplier) { try { return supplier.get(); } catch (Exception ex) { return null; } }
    private void safeRun(ThrowingRunnable runnable) { try { runnable.run(); } catch (Exception ignored) {} }

    @FunctionalInterface private interface ThrowingSupplier<T> { T get() throws Exception; }
    @FunctionalInterface private interface ThrowingRunnable { void run() throws Exception; }
}
