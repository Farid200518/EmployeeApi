package com.Task.EmployeeAPI.Exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApplicationException{
    public NotFoundException(String message) {
        super("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
