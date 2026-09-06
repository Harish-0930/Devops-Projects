package com.nexusbank.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private Long totalCustomers;
    private Long totalAccounts;
    private Long totalTransactions;
    private Long totalLoans;
    private Long pendingAccountApprovals;
    private Long pendingLoanApprovals;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
}