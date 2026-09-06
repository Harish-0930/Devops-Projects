package com.nexusbank.admin.controller;

import com.nexusbank.admin.dto.AdminLoginRequest;
import com.nexusbank.admin.dto.AdminLoginResponse;
import com.nexusbank.admin.service.AdminAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired private AdminAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}