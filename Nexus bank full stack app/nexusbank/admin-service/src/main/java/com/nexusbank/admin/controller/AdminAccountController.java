package com.nexusbank.admin.controller;

import com.nexusbank.admin.service.AdminAccountService;
import com.nexusbank.customer.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    @Autowired private AdminAccountService accountService;

    @GetMapping public ResponseEntity<List<AccountDTO>> getAll() { return ResponseEntity.ok(accountService.getAllAccounts()); }
    @GetMapping("/customer/{customerId}") public ResponseEntity<List<AccountDTO>> getByCustomer(@PathVariable Long customerId) { return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId)); }
    @GetMapping("/{accountNumber}") public ResponseEntity<AccountDTO> getByNumber(@PathVariable String accountNumber) { return ResponseEntity.ok(accountService.getAccount(accountNumber)); }
    @PutMapping("/{accountNumber}/approve") public ResponseEntity<Void> approve(@PathVariable String accountNumber) { accountService.approveAccount(accountNumber); return ResponseEntity.ok().build(); }
    @PutMapping("/{accountNumber}/reject") public ResponseEntity<Void> reject(@PathVariable String accountNumber) { accountService.rejectAccount(accountNumber); return ResponseEntity.ok().build(); }
    @PutMapping("/{accountNumber}/freeze") public ResponseEntity<Void> freeze(@PathVariable String accountNumber) { accountService.freezeAccount(accountNumber); return ResponseEntity.ok().build(); }
    @PutMapping("/{accountNumber}/unfreeze") public ResponseEntity<Void> unfreeze(@PathVariable String accountNumber) { accountService.unfreezeAccount(accountNumber); return ResponseEntity.ok().build(); }
}
