package com.Task.employeeAPI.payload;

import com.Task.employeeAPI.dao.Enums.Status;
import lombok.Data;

@Data
public class TaskWorkflowPayload {
    private Status status;
    private Integer taskId;
}
