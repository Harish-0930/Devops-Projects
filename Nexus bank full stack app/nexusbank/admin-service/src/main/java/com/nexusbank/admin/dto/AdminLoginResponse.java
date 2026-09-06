package com.nexusbank.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    private String token;
    private String type = "Bearer";
    private Long adminId;
    private String username;
    private String role;
    private String message;
}