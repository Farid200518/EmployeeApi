package com.Task.EmployeeAPI.DTO;

import com.Task.EmployeeAPI.DAO.Enums.Status;
import lombok.Data;

@Data
public class TaskWorkflowDTO {
    private Status status;
}
