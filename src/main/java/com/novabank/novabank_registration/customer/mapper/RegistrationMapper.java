package com.novabank.novabank_registration.customer.mapper;

import com.novabank.novabank_registration.customer.dto.RegistrationResponseDto;
import com.novabank.novabank_registration.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public RegistrationResponseDto toResponseDto(Customer customer) {

        RegistrationResponseDto response =
                new RegistrationResponseDto();

        response.setCustomerId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setStatus(customer.getStatus());
        response.setRegisteredAt(customer.getCreatedAt());

        return response;
    }
}