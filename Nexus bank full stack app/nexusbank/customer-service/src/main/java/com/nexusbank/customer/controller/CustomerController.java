package com.nexusbank.customer.controller;

import com.nexusbank.customer.dto.*;
import com.nexusbank.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerDTO> register(@Valid @RequestBody CustomerRegistrationRequest request) {
        CustomerDTO dto = CustomerDTO.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .username(request.getUsername())
                .address(request.getAddress())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.registerCustomer(dto, request.getPassword()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(customerService.login(request));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDTO> getProfile(@RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(customerService.getCustomerByUsername(username));
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDTO> updateCustomer(@RequestHeader("X-User-Name") String username, 
                                                       @RequestBody CustomerDTO dto) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.ok(customerService.updateCustomer(customer.getCustomerId(), dto));
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> updatePassword(@RequestHeader("X-User-Name") String username,
                                                @RequestBody PasswordUpdateRequest request) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        customerService.updatePassword(customer.getCustomerId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/{customerId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateCustomer(@PathVariable Long customerId) {
        customerService.activateCustomer(customerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{customerId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable Long customerId) {
        customerService.deactivateCustomer(customerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/address")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> addAddress(@RequestHeader("X-User-Name") String username,
                                                  @RequestBody AddressDTO dto) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addAddress(customer.getCustomerId(), dto));
    }

    @PutMapping("/address")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> updateAddress(@RequestHeader("X-User-Name") String username,
                                                     @RequestBody AddressDTO dto) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.ok(customerService.updateAddress(customer.getCustomerId(), dto));
    }

    @GetMapping("/address")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AddressDTO> getAddress(@RequestHeader("X-User-Name") String username) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.ok(customerService.getAddress(customer.getCustomerId()));
    }

    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> getCustomerByAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(customerService.getCustomerByAccountNumber(accountNumber));
    }

    // Inner DTOs for requests
    @lombok.Data
    public static class CustomerRegistrationRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String username;
        private String password;
        private AddressDTO address;
    }

    @lombok.Data
    public static class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;
    }
}