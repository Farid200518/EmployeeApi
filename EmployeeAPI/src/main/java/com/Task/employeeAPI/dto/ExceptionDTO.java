package com.Task.employeeAPI.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ExceptionDTO {
    private final String code;
    private final String message;
    private final HttpStatus status;

}
