package com.nexusbank.customer.service;

import com.nexusbank.customer.dto.DashboardDTO;

public interface DashboardService {
    DashboardDTO getCustomerDashboard(Long customerId);
}