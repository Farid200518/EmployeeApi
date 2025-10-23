package com.Task.employeeAPI.dao.Enums;

import com.Task.employeeAPI.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH;

    @JsonCreator
    public static Priority customPriorityCreator(Object value) {
        if (value instanceof String s) {
            try {
                return Priority.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ApplicationException(
                        "INVALID_PRIORITY",
                        "Invalid priority string: " + s,
                        HttpStatus.BAD_REQUEST
                );
            }
        } else if (value instanceof Integer n) {
            if (n >= 0 && n < Priority.values().length) {
                return Priority.values()[n];
            } else {
                throw new ApplicationException(
                        "INVALID_PRIORITY",
                        "Invalid priority index: " + n,
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        throw new ApplicationException(
                "INVALID_PRIORITY",
                "Invalid priority type: " + value,
                HttpStatus.BAD_REQUEST
        );
    }
}
