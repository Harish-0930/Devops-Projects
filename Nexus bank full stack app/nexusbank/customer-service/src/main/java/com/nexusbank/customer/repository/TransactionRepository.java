package com.nexusbank.customer.repository;

import com.nexusbank.customer.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE (t.transactionType = com.nexusbank.customer.enums.TransactionType.DEBIT AND t.fromAccount.accountNumber = :accountNumber) OR (t.transactionType = com.nexusbank.customer.enums.TransactionType.CREDIT AND t.toAccount.accountNumber = :accountNumber) ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT t FROM Transaction t WHERE (t.transactionType = com.nexusbank.customer.enums.TransactionType.DEBIT AND t.fromAccount.customer.customerId = :customerId) OR (t.transactionType = com.nexusbank.customer.enums.TransactionType.CREDIT AND t.toAccount.customer.customerId = :customerId) ORDER BY t.transactionDate DESC")
    List<Transaction> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT t FROM Transaction t WHERE (t.transactionType = com.nexusbank.customer.enums.TransactionType.DEBIT AND t.fromAccount.accountNumber = :accountNumber) OR (t.transactionType = com.nexusbank.customer.enums.TransactionType.CREDIT AND t.toAccount.accountNumber = :accountNumber) ORDER BY t.transactionDate DESC LIMIT 10")
    List<Transaction> findMiniStatementByAccountNumber(@Param("accountNumber") String accountNumber);

    long countByFromAccountCustomerCustomerIdOrToAccountCustomerCustomerId(Long fromCustomerId, Long toCustomerId);
}
