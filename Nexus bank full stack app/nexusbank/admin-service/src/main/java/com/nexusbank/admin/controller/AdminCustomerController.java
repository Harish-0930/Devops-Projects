package com.nexusbank.admin.controller;

import com.nexusbank.admin.service.AdminCustomerService;
import com.nexusbank.customer.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    @Autowired private AdminCustomerService customerService;

    @GetMapping public ResponseEntity<List<CustomerDTO>> getAll() { return ResponseEntity.ok(customerService.getAllCustomers()); }
    @GetMapping("/{id}") public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) { return ResponseEntity.ok(customerService.getCustomerById(id)); }
    @GetMapping("/account/{accountNumber}") public ResponseEntity<CustomerDTO> getByAccount(@PathVariable String accountNumber) { return ResponseEntity.ok(customerService.getCustomerByAccountNumber(accountNumber)); }
    @PutMapping("/{id}/activate") public ResponseEntity<Void> activate(@PathVariable Long id) { customerService.activateCustomer(id); return ResponseEntity.ok().build(); }
    @PutMapping("/{id}/deactivate") public ResponseEntity<Void> deactivate(@PathVariable Long id) { customerService.deactivateCustomer(id); return ResponseEntity.ok().build(); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { customerService.deleteCustomer(id); return ResponseEntity.noContent().build(); }
}