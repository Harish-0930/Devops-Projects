package com.nexusbank.admin.controller;

import com.nexusbank.admin.service.AdminTransactionService;
import com.nexusbank.customer.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTransactionController {

    @Autowired private AdminTransactionService transactionService;

    @GetMapping public ResponseEntity<List<TransactionDTO>> getAll() { return ResponseEntity.ok(transactionService.getAllTransactions()); }
    @GetMapping("/customer/{customerId}") public ResponseEntity<List<TransactionDTO>> getByCustomer(@PathVariable Long customerId) { return ResponseEntity.ok(transactionService.getCustomerTransactions(customerId)); }
    @GetMapping("/account/{accountNumber}") public ResponseEntity<List<TransactionDTO>> getByAccount(@PathVariable String accountNumber) { return ResponseEntity.ok(transactionService.getAccountTransactions(accountNumber)); }
}