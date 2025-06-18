package com.Task.EmployeeAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
public class ExceptionDTO {
    private final String code;
    private final String message;
    private final HttpStatus status;

}
