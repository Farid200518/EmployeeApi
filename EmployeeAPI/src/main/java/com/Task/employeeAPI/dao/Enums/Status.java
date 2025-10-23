package com.Task.employeeAPI.dao.Enums;

import com.Task.employeeAPI.exceptions.ApplicationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum Status {
    CREATED,
    IN_PROGRESS,
    RESOLVED,
    DONE;

    @JsonCreator
    public static Status customRoleCreator(Object value) {
        if (value instanceof String s) {
            try {
                return Status.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ApplicationException(
                        "INVALID_STATUS",
                        "Invalid status string: " + s,
                        HttpStatus.BAD_REQUEST
                );
            }
        } else if (value instanceof Integer n) {
            if (n >= 0 && n < Status.values().length) {
                return Status.values()[n];
            } else {
                throw new ApplicationException(
                        "INVALID_STATUS",
                        "Invalid status index: " + n,
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        throw new ApplicationException(
                "INVALID_STATUS",
                "Invalid status type: " + value,
                HttpStatus.BAD_REQUEST
        );
    }
}
