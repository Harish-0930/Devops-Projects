package com.nexusbank.admin.service.impl;

import com.nexusbank.admin.feign.AccountFeignClient;
import com.nexusbank.admin.feign.CustomerFeignClient;
import com.nexusbank.admin.feign.LoanFeignClient;
import com.nexusbank.admin.feign.TransactionFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {

    @Mock
    private CustomerFeignClient customerFeignClient;

    @Mock
    private AccountFeignClient accountFeignClient;

    @Mock
    private LoanFeignClient loanFeignClient;

    @Mock
    private TransactionFeignClient transactionFeignClient;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    @Test
    void getDashboardShouldReturnZeroedStatsWhenDownstreamCallsFail() {
        when(customerFeignClient.getAllCustomers()).thenThrow(new RuntimeException("customer-service unavailable"));
        when(accountFeignClient.getAllAccounts()).thenThrow(new RuntimeException("account-service unavailable"));
        when(loanFeignClient.getAllLoans()).thenThrow(new RuntimeException("loan-service unavailable"));
        when(transactionFeignClient.getAllTransactions()).thenThrow(new RuntimeException("transaction-service unavailable"));

        assertDoesNotThrow(() -> {
            var dashboard = adminDashboardService.getDashboard();
            assertEquals(0L, dashboard.getTotalCustomers());
            assertEquals(0L, dashboard.getTotalAccounts());
            assertEquals(0L, dashboard.getTotalTransactions());
            assertEquals(0L, dashboard.getTotalLoans());
            assertEquals(0L, dashboard.getPendingAccountApprovals());
            assertEquals(0L, dashboard.getPendingLoanApprovals());
            assertEquals(0, dashboard.getTotalDeposits().intValueExact());
            assertEquals(0, dashboard.getTotalWithdrawals().intValueExact());
        });
    }
}
