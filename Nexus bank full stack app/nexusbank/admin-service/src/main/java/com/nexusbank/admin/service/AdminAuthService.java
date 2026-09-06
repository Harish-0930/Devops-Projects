package com.nexusbank.admin.service;

import com.nexusbank.admin.dto.AdminLoginRequest;
import com.nexusbank.admin.dto.AdminLoginResponse;

public interface AdminAuthService {
    AdminLoginResponse login(AdminLoginRequest request);
}