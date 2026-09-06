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
public class CustomerSummaryDTO {
    private Long customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private Long accountCount;
    private BigDecimal totalBalance;
}