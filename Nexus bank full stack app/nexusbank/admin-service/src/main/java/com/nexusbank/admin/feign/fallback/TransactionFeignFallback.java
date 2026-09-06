package com.nexusbank.admin.feign.fallback;

import com.nexusbank.customer.dto.TransactionDTO;
import com.nexusbank.admin.feign.TransactionFeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TransactionFeignFallback implements TransactionFeignClient {
    @Override public List<TransactionDTO> getAllTransactions() { return Collections.emptyList(); }
    @Override public List<TransactionDTO> getCustomerTransactions(Long id) { return Collections.emptyList(); }
    @Override public List<TransactionDTO> getAccountTransactions(String num) { return Collections.emptyList(); }
}