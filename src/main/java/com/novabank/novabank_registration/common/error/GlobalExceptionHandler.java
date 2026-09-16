package com.novabank.novabank_registration.common.error;

import com.novabank.novabank_registration.common.logging.LogMasker;
import com.novabank.novabank_registration.customer.dto.RegistrationRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        // Safe logging
        Object target =
                exception.getBindingResult()
                        .getTarget();

        if (target instanceof RegistrationRequestDto) {

            RegistrationRequestDto registrationRequest =
                    (RegistrationRequestDto) target;

            log.debug(
                    "Registration validation failed: email={}, "
                            + "password=***, confirmPassword=***, "
                            + "errorCount={}",
                    LogMasker.maskEmail(
                            registrationRequest.getEmail()
                    ),
                    exception.getBindingResult()
                            .getErrorCount()
            );

        } else {

            log.debug(
                    "Request validation failed: path={}, errorCount={}",
                    request.getRequestURI(),
                    exception.getBindingResult()
                            .getErrorCount()
            );
        }


        // 1. Collect normal field errors FIRST
        List<FieldErrorDto> fieldErrors =
                new ArrayList<>(
                        exception.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error ->
                                        new FieldErrorDto(
                                                error.getField(),
                                                error.getDefaultMessage()
                                        )
                                )
                                .toList()
                );


        // 2. Check password mismatch
        boolean passwordMismatch =
                exception.getBindingResult()
                        .getGlobalErrors()
                        .stream()
                        .anyMatch(error ->
                                "PasswordsMatch"
                                        .equals(error.getCode())
                        );


        // 3. Check password equals email
        boolean passwordEqualsEmail =
                exception.getBindingResult()
                        .getGlobalErrors()
                        .stream()
                        .anyMatch(error ->
                                "PasswordNotEmail"
                                        .equals(error.getCode())
                        );


        // 4. Convert cross-field errors into fieldErrors
        if (passwordMismatch) {

            fieldErrors.add(
                    new FieldErrorDto(
                            "confirmPassword",
                            "Passwords do not match"
                    )
            );
        }

        if (passwordEqualsEmail) {

            fieldErrors.add(
                    new FieldErrorDto(
                            "password",
                            "Password must not be the same as email"
                    )
            );
        }


        // 5. Choose the main error code
        String errorCode =
                passwordMismatch
                        ? "PASSWORD_MISMATCH"
                        : "VALIDATION_FAILED";

        String errorMessage =
                passwordMismatch
                        ? "Passwords do not match"
                        : "Request validation failed";


        ApiErrorResponse response =
                new ApiErrorResponse(
                        request.getRequestURI(),
                        errorCode,
                        errorMessage,
                        fieldErrors
                );

        return ResponseEntity
                .badRequest()
                .body(response);
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse>
    handleDuplicateResourceException(
            DuplicateResourceException exception,
            HttpServletRequest request) {

        ApiErrorResponse response =
                new ApiErrorResponse(
                        request.getRequestURI(),
                        exception.getErrorCode(),
                        exception.getMessage(),
                        List.of()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse>
    handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        ApiErrorResponse response =
                new ApiErrorResponse(
                        request.getRequestURI(),
                        "DATA_INTEGRITY_CONFLICT",
                        "The submitted data conflicts with an existing record",
                        List.of()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}