package com.Task.EmployeeAPI.DTO;

import com.Task.EmployeeAPI.DAO.Enums.Priority;
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

    @NotNull(message = "Employee ID must not be null!")
    @Positive(message = "Employee ID must be a positive number!")
    private Integer employeeId;
}

