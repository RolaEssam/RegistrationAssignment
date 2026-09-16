package com.novabank.novabank_registration.customer.validation;


import com.novabank.novabank_registration.customer.dto.RegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator
        implements ConstraintValidator<
        PasswordsMatch,
        RegistrationRequestDto> {

    @Override
    public boolean isValid(
            RegistrationRequestDto request,
            ConstraintValidatorContext context) {

        if (request == null) {
            return true;
        }

        String password = request.getPassword();
        String confirmPassword =
                request.getConfirmPassword();

        if (password == null || confirmPassword == null) {
            return true;
        }

        return password.equals(confirmPassword);
    }
}