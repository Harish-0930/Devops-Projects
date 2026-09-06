package com.nexusbank.customer.controller;

import com.nexusbank.customer.dto.*;
import com.nexusbank.customer.service.CustomerService;
import com.nexusbank.customer.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<DashboardDTO> getDashboard(@RequestHeader("X-User-Name") String username) {
        CustomerDTO customer = customerService.getCustomerByUsername(username);
        return ResponseEntity.ok(dashboardService.getCustomerDashboard(customer.getCustomerId()));
    }
}