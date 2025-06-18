package com.Task.EmployeeAPI.DAO.Enums;

import com.Task.EmployeeAPI.Exceptions.ApplicationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum Role {
    EMPLOYEE,
    HR,
    HR_MANAGER,
    HEAD_MANAGER;

    @JsonCreator
    public static Role customRoleCreator(Object value) {
        if (value instanceof String s) {
            try {
                return Role.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ApplicationException(
                        "INVALID_ROLE",
                        "Invalid role string: " + s,
                        HttpStatus.BAD_REQUEST
                );
            }
        } else if (value instanceof Integer n) {
            if (n >= 0 && n < Role.values().length) {
                return Role.values()[n];
            } else {
                throw new ApplicationException(
                        "INVALID_ROLE",
                        "Invalid role index: " + n,
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        throw new ApplicationException(
                "INVALID_ROLE",
                "Invalid role type: " + value,
                HttpStatus.BAD_REQUEST
        );
    }
}
