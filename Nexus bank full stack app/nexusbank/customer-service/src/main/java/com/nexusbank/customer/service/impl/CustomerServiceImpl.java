package com.nexusbank.customer.service.impl;

import com.nexusbank.customer.dto.*;
import com.nexusbank.customer.entity.Address;
import com.nexusbank.customer.entity.Customer;
import com.nexusbank.customer.enums.CustomerStatus;
import com.nexusbank.customer.exception.BadRequestException;
import com.nexusbank.customer.exception.ResourceNotFoundException;
import com.nexusbank.customer.repository.AddressRepository;
import com.nexusbank.customer.repository.CustomerRepository;
import com.nexusbank.customer.security.JwtTokenProvider;
import com.nexusbank.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public CustomerDTO registerCustomer(CustomerDTO dto, String password) {
        if (customerRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new BadRequestException("Phone number already exists");
        }

        Customer customer = Customer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(password))
                .status(CustomerStatus.ACTIVE)
                .build();

        if (dto.getAddress() != null) {
            Address addr = Address.builder()
                    .street(dto.getAddress().getStreet())
                    .city(dto.getAddress().getCity())
                    .state(dto.getAddress().getState())
                    .country(dto.getAddress().getCountry())
                    .pincode(dto.getAddress().getPincode())
                    .build();
            customer.setAddress(addr);
        }

        Customer saved = customerRepository.save(customer);
        return mapToDTO(saved);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(request.getUsername(), "CUSTOMER");
        Customer customer = customerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return LoginResponse.builder()
                .token(token)
                .customerId(customer.getCustomerId())
                .username(customer.getUsername())
                .message("Login successful")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return mapToDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByUsername(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + username));
        return mapToDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());

        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    public void updatePassword(Long customerId, String oldPassword, String newPassword) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void activateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
    }

    @Override
    public void deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public AddressDTO addAddress(Long customerId, AddressDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Address address = Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .pincode(dto.getPincode())
                .build();

        customer.setAddress(address);
        customerRepository.save(customer);
        return mapToAddressDTO(address);
    }

    @Override
    public AddressDTO updateAddress(Long customerId, AddressDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Address address = customer.getAddress();
        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPincode(dto.getPincode());

        return mapToAddressDTO(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddress(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (customer.getAddress() == null) {
            throw new ResourceNotFoundException("Address not found");
        }
        return mapToAddressDTO(customer.getAddress());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByAccountNumber(String accountNumber) {
        return customerRepository.findAll().stream()
                .filter(c -> c.getAccounts() != null && 
                        c.getAccounts().stream().anyMatch(a -> a.getAccountNumber().equals(accountNumber)))
                .findFirst()
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for account: " + accountNumber));
    }

    private CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .uniqueId(customer.getUniqueId())
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .username(customer.getUsername())
                .status(customer.getStatus())
                .createdDate(customer.getCreatedDate())
                .address(customer.getAddress() != null ? mapToAddressDTO(customer.getAddress()) : null)
                .build();
    }

    private AddressDTO mapToAddressDTO(Address address) {
        return AddressDTO.builder()
                .addressId(address.getAddressId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .pincode(address.getPincode())
                .build();
    }
}