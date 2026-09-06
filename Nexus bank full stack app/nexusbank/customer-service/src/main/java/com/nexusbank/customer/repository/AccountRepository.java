package com.nexusbank.customer.repository;

import com.nexusbank.customer.entity.Account;
import com.nexusbank.customer.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByCustomerCustomerId(Long customerId);
    List<Account> findByAccountStatus(AccountStatus status);
    Optional<Account> findByAccountNumberAndCustomerCustomerId(String accountNumber, Long customerId);
    long countByAccountStatus(AccountStatus status);
}