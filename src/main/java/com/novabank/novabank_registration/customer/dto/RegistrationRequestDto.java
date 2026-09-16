package com.novabank.novabank_registration.customer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.novabank.novabank_registration.customer.validation.PasswordsMatch;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
@PasswordsMatch
public class RegistrationRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(example = "Rola")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(example = "Essam")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(example = "rola@example.com")
    private String email;

    public void setEmail(String email) {
        this.email = email == null
                ? null
                : email.trim();
    }

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^\\d{10}$",
            message = "Mobile number must contain exactly 10 digits"
    )
    @Schema(example = "0123456789")
    private String mobileNumber;

    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            message = "Password must be at least 8 characters"
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Password must contain at least one letter and one digit"
    )
    @Schema(example = "Password123")
    @ToString.Exclude
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Schema(example = "Password123")
    @ToString.Exclude
    private String confirmPassword;
}