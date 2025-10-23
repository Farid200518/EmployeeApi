package com.Task.employeeAPI.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerError extends ApplicationException{
    public InternalServerError(String message) {
        super("INTERNAL_SERVER", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
