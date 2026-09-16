package com.novabank.novabank_registration.common.error;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorDto {

    private String field;
    private String message;
}