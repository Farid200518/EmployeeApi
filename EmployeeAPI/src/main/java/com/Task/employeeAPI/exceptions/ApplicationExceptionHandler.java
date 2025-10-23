package com.Task.employeeAPI.exceptions;

import com.Task.employeeAPI.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleApplicationException(ApplicationException applicationException) {
        return new ResponseEntity<>(applicationException.toExceptionDto(), applicationException.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleValidationError(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().getFirst();
        ExceptionDTO exceptionDTO = new ExceptionDTO("VALIDATION_ERROR", fieldError.getDefaultMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exceptionDTO);
    }
}
