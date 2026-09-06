package com.nexusbank.customer.dto;

import com.nexusbank.customer.enums.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long customerId;
    private String uniqueId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private CustomerStatus status;
    private LocalDateTime createdDate;
    private AddressDTO address;
}