package com.Task.employeeAPI.exceptions;

import com.Task.employeeAPI.dto.ExceptionDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApplicationException extends RuntimeException{
    private final String code;
    private final String msg;
    private final HttpStatus status;

    public ExceptionDTO toExceptionDto(){
        return new ExceptionDTO(
                this.code,
                this.msg,
                this.status
        );
    }

}
