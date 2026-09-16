package com.novabank.novabank_registration.customer.service;

import com.novabank.novabank_registration.common.error.DuplicateResourceException;
import com.novabank.novabank_registration.common.logging.LogMasker;
import com.novabank.novabank_registration.customer.dto.RegistrationRequestDto;
import com.novabank.novabank_registration.customer.dto.RegistrationResponseDto;
import com.novabank.novabank_registration.customer.entity.Customer;
import com.novabank.novabank_registration.customer.entity.CustomerStatus;
import com.novabank.novabank_registration.customer.entity.Role;
import com.novabank.novabank_registration.customer.entity.RoleName;
import com.novabank.novabank_registration.customer.mapper.RegistrationMapper;
import com.novabank.novabank_registration.customer.repository.CustomerRepository;

import com.novabank.novabank_registration.customer.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RegistrationService {

    private static final Logger log =
            LoggerFactory.getLogger(RegistrationService.class);
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            RoleRepository roleRepository, CustomerRepository customerRepository,
            RegistrationMapper registrationMapper,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;

        this.customerRepository = customerRepository;
        this.registrationMapper = registrationMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResponseDto register(
            RegistrationRequestDto request) {

        String normalizedEmail =
                request.getEmail()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        String normalizedMobile =
                request.getMobileNumber().trim();

        if (customerRepository
                .existsByEmailIgnoreCase(normalizedEmail)) {

            throw new DuplicateResourceException(
                    "EMAIL_ALREADY_REGISTERED",
                    "Email is already registered"
            );
        }

        if (customerRepository
                .existsByMobileNumber(normalizedMobile)) {

            throw new DuplicateResourceException(
                    "MOBILE_ALREADY_REGISTERED",
                    "Mobile number is already registered"
            );
        }

        Customer customer = new Customer();
        Role customerRole =
                roleRepository
                        .findByName(RoleName.CUSTOMER)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "CUSTOMER role is not configured"
                                )
                        );
        customer.getRoles()
                .add(customerRole);

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(normalizedEmail);
        customer.setMobileNumber(normalizedMobile);

        String passwordHash =
                passwordEncoder.encode(
                        request.getPassword()
                );

        customer.setPasswordHash(passwordHash);

        customer.setStatus(
                CustomerStatus.PENDING_VERIFICATION
        );



        Customer savedCustomer;

        try {

            savedCustomer =
                    customerRepository.save(customer);

        } catch (DataIntegrityViolationException exception) {

            if (customerRepository
                    .existsByEmailIgnoreCase(normalizedEmail)) {

                throw new DuplicateResourceException(
                        "EMAIL_ALREADY_REGISTERED",
                        "Email is already registered"
                );
            }

            if (customerRepository
                    .existsByMobileNumber(normalizedMobile)) {

                throw new DuplicateResourceException(
                        "MOBILE_ALREADY_REGISTERED",
                        "Mobile number is already registered"
                );
            }

            throw exception;
        }
        log.info(
                "Customer registered successfully: customerId={}, email={}",
                savedCustomer.getId(),
                LogMasker.maskEmail(savedCustomer.getEmail())
        );

        return registrationMapper
                .toResponseDto(savedCustomer);

    }
}