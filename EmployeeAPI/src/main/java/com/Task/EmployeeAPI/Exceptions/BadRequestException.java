package com.Task.EmployeeAPI.Exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApplicationException{
    public BadRequestException(String message) {
        super("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }
}
