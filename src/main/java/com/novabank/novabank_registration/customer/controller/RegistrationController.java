package com.novabank.novabank_registration.customer.controller;


import com.novabank.novabank_registration.customer.dto.RegistrationRequestDto;
import com.novabank.novabank_registration.customer.dto.RegistrationResponseDto;
import com.novabank.novabank_registration.customer.service.RegistrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(
            RegistrationService registrationService) {

        this.registrationService = registrationService;
    }

    @Operation
            (
            summary = "Register a new NovaBank customer",
            description = "Creates a new customer with PENDING_VERIFICATION status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer registered successfully"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(
            @Valid @RequestBody RegistrationRequestDto request) {

        RegistrationResponseDto response =
                registrationService.register(request);

        URI location = URI.create(
                "/api/v1/customers/"
                        + response.getCustomerId()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }
}