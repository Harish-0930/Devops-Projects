package com.nexusbank.admin.controller;

import com.nexusbank.admin.service.AdminLoanService;
import com.nexusbank.customer.dto.LoanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanController {

    @Autowired private AdminLoanService loanService;

    @GetMapping public ResponseEntity<List<LoanDTO>> getAll() { return ResponseEntity.ok(loanService.getAllLoans()); }
    @GetMapping("/pending") public ResponseEntity<List<LoanDTO>> getPending() { return ResponseEntity.ok(loanService.getPendingLoans()); }
    @GetMapping("/customer/{customerId}") public ResponseEntity<List<LoanDTO>> getByCustomer(@PathVariable Long customerId) { return ResponseEntity.ok(loanService.getLoansByCustomerId(customerId)); }
    @GetMapping("/account/{accountNumber}") public ResponseEntity<List<LoanDTO>> getByAccount(@PathVariable String accountNumber) { return ResponseEntity.ok(loanService.getLoansByAccountNumber(accountNumber)); }
    @GetMapping("/{loanId}") public ResponseEntity<LoanDTO> getById(@PathVariable Long loanId) { return ResponseEntity.ok(loanService.getLoanById(loanId)); }
    @PutMapping("/{loanId}/approve") public ResponseEntity<Void> approve(@PathVariable Long loanId) { loanService.approveLoan(loanId); return ResponseEntity.ok().build(); }
    @PutMapping("/{loanId}/reject") public ResponseEntity<Void> reject(@PathVariable Long loanId) { loanService.rejectLoan(loanId); return ResponseEntity.ok().build(); }
}
