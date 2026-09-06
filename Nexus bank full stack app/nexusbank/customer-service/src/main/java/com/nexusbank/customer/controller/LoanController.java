package com.nexusbank.customer.controller;

import com.nexusbank.customer.dto.*;
import com.nexusbank.customer.service.CustomerService;
import com.nexusbank.customer.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<LoanDTO> applyLoan(@RequestHeader("X-User-Name") String username, @Valid @RequestBody LoanDTO dto) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.applyLoan(customer.getCustomerId(), dto));
    }

    @GetMapping("/status/{loanId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<LoanDTO> getLoanStatus(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanById(loanId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<LoanDTO>> getCustomerLoans(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanService.getLoansByCustomerId(customerId));
    }

    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanDTO>> getAccountLoans(@PathVariable String accountNumber) {
        return ResponseEntity.ok(loanService.getLoansByAccountNumber(accountNumber));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanDTO>> getPendingLoans() {
        return ResponseEntity.ok(loanService.getPendingLoans());
    }

    @PutMapping("/{loanId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveLoan(@PathVariable Long loanId) {
        loanService.approveLoan(loanId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{loanId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectLoan(@PathVariable Long loanId) {
        loanService.rejectLoan(loanId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{loanId}/disburse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disburseLoan(@PathVariable Long loanId) {
        loanService.disburseLoan(loanId);
        return ResponseEntity.ok().build();
    }
}
