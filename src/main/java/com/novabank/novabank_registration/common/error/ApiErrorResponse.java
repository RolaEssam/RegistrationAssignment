package com.novabank.novabank_registration.common.error;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final String apiPath;
    private final String errorCode;
    private final String errorMessage;
    private final LocalDateTime errorTime;
    private final List<FieldErrorDto> fieldErrors;

    public ApiErrorResponse(
            String apiPath,
            String errorCode,
            String errorMessage) {

        this(
                apiPath,
                errorCode,
                errorMessage,
                null
        );
    }

    public ApiErrorResponse(
            String apiPath,
            String errorCode,
            String errorMessage,
            List<FieldErrorDto> fieldErrors) {

        this.apiPath = apiPath;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorTime = LocalDateTime.now();
        this.fieldErrors = fieldErrors;
    }
}