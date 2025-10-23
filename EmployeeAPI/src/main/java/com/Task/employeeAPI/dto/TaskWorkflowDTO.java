package com.Task.employeeAPI.dto;

import com.Task.employeeAPI.dao.Enums.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskWorkflowDTO {
    private Integer id;
    private Status status;
    private Integer updatedById;
    private Integer taskId;
    private LocalDateTime lastUpdated;
}
