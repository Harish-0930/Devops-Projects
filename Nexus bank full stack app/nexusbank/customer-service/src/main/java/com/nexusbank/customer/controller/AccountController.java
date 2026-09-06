package com.nexusbank.customer.controller;

import com.nexusbank.customer.dto.AccountDTO;
import com.nexusbank.customer.service.AccountService;
import com.nexusbank.customer.service.CustomerService;
import com.nexusbank.customer.dto.CustomerDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AccountDTO> createAccount(@RequestHeader("X-User-Name") String username,
                                                     @Valid @RequestBody AccountDTO dto) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(customer.getCustomerId(), dto));
    }

    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<AccountDTO>> getCustomerAccounts(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PutMapping("/{accountNumber}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveAccount(@PathVariable String accountNumber) {
        accountService.approveAccount(accountNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountNumber}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectAccount(@PathVariable String accountNumber) {
        accountService.rejectAccount(accountNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountNumber}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> freezeAccount(@PathVariable String accountNumber) {
        accountService.freezeAccount(accountNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountNumber}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unfreezeAccount(@PathVariable String accountNumber) {
        accountService.unfreezeAccount(accountNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountNumber}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> closeAccount(@PathVariable String accountNumber) {
        accountService.closeAccount(accountNumber);
        return ResponseEntity.ok().build();
    }
}
