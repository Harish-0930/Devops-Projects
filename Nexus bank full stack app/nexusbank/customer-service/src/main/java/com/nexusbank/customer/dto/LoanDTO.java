package com.nexusbank.customer.dto;

import com.nexusbank.customer.enums.LoanStatus;
import com.nexusbank.customer.enums.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long loanId;
    private LoanType loanType;
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "0.01", message = "Loan amount must be positive")
    private BigDecimal amount;
    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least one month")
    private Integer tenure;
    private BigDecimal interestRate;
    private LocalDateTime appliedDate;
    private LoanStatus loanStatus;
    private Long customerId;
}
