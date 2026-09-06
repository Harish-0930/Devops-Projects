package com.nexusbank.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private CustomerDTO customer;
    private List<AccountDTO> accounts;
    private BigDecimal totalBalance;
    private List<TransactionDTO> recentTransactions;
    private List<LoanDTO> activeLoans;
    private Long totalAccounts;
    private Long totalTransactions;
    private Long totalLoans;
}