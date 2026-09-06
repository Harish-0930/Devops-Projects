package com.nexusbank.admin.service.impl;

import com.nexusbank.admin.dto.AdminLoginRequest;
import com.nexusbank.admin.dto.AdminLoginResponse;
import com.nexusbank.admin.entity.Admin;
import com.nexusbank.admin.repository.AdminRepository;
import com.nexusbank.admin.security.JwtTokenProvider;
import com.nexusbank.admin.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired private AdminRepository adminRepository;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(request.getUsername(), "ADMIN");
        Admin admin = adminRepository.findByUsername(request.getUsername()).orElseThrow();

        return AdminLoginResponse.builder()
                .token(token)
                .adminId(admin.getAdminId())
                .username(admin.getUsername())
                .role(admin.getRole().name())
                .message("Login successful")
                .build();
    }
}