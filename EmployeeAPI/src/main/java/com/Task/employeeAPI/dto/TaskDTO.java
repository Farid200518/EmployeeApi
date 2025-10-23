package com.Task.employeeAPI.dto;

import com.Task.employeeAPI.dao.Enums.Priority;
import com.Task.employeeAPI.dao.Enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTO {

    private Integer id;

    @NotBlank(message = "Description must not be blank!")
    @Size(max = 100, message = "Description is too long!")
    private String description;

    @NotNull(message = "Priority must not be null!")
    private Priority priority;

    private Status status;

    @NotNull(message = "Employee ID must not be null!")
    @Positive(message = "Employee ID must be a positive number!")
    private Integer employeeId;
}

