package com.Task.EmployeeAPI.Exceptions;

import org.springframework.http.HttpStatus;

public class BadCredentialsException extends ApplicationException{
    public BadCredentialsException(String message) {
        super("BAD_CREDENTIALS", message, HttpStatus.BAD_REQUEST);
    }
}
