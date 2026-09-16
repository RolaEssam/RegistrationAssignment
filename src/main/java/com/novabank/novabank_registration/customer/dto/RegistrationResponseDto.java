package com.novabank.novabank_registration.customer.dto;


import com.novabank.novabank_registration.customer.entity.CustomerStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationResponseDto {

    private Long customerId;

    private String firstName;

    private String lastName;

    private String email;

    private CustomerStatus status;

    private LocalDateTime registeredAt;
}