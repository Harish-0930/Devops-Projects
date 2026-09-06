package com.nexusbank.admin.service.impl;

import com.nexusbank.admin.feign.CustomerFeignClient;
import com.nexusbank.admin.service.AdminCustomerService;
import com.nexusbank.customer.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCustomerServiceImpl implements AdminCustomerService {

    @Autowired private CustomerFeignClient customerFeignClient;

    @Override public List<CustomerDTO> getAllCustomers() { return safeList(() -> customerFeignClient.getAllCustomers()); }
    @Override public CustomerDTO getCustomerById(Long id) { return safeGet(() -> customerFeignClient.getCustomerById(id)); }
    @Override public CustomerDTO getCustomerByAccountNumber(String acc) { return safeGet(() -> customerFeignClient.getCustomerByAccountNumber(acc)); }
    @Override public void activateCustomer(Long id) { safeRun(() -> customerFeignClient.activateCustomer(id)); }
    @Override public void deactivateCustomer(Long id) { safeRun(() -> customerFeignClient.deactivateCustomer(id)); }
    @Override public void deleteCustomer(Long id) { safeRun(() -> customerFeignClient.deleteCustomer(id)); }

    private <T> List<T> safeList(ThrowingSupplier<List<T>> supplier) { try { return supplier.get(); } catch (Exception ex) { return List.of(); } }
    private <T> T safeGet(ThrowingSupplier<T> supplier) { try { return supplier.get(); } catch (Exception ex) { return null; } }
    private void safeRun(ThrowingRunnable runnable) { try { runnable.run(); } catch (Exception ignored) {} }

    @FunctionalInterface private interface ThrowingSupplier<T> { T get() throws Exception; }
    @FunctionalInterface private interface ThrowingRunnable { void run() throws Exception; }
}