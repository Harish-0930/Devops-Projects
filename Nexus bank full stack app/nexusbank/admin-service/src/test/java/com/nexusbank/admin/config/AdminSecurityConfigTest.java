package com.nexusbank.admin.config;

import com.nexusbank.admin.controller.AdminAuthController;
import com.nexusbank.admin.security.JwtAuthenticationFilter;
import com.nexusbank.admin.service.AdminAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuthController.class)
@Import(SecurityConfig.class)
class AdminSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAuthService adminAuthService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithAnonymousUser
    void adminLoginEndpointShouldBeAccessibleWithoutAdminRole() throws Exception {
        mockMvc.perform(post("/api/admin/login"))
                .andExpect(status().isOk());
    }
}
