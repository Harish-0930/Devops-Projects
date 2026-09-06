package com.nexusbank.customer.dto;

import com.nexusbank.customer.enums.AccountStatus;
import com.nexusbank.customer.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private String accountNumber;
    private AccountType accountType;
    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.00", message = "Initial balance cannot be negative")
    private BigDecimal balance;
    private AccountStatus accountStatus;
    private LocalDateTime createdDate;
    private Long customerId;
}
