package com.Task.employeeAPI.services.abstraction;

import com.Task.employeeAPI.dto.TaskWorkflowDTO;
import com.Task.employeeAPI.payload.TaskWorkflowPayload;

import java.util.List;

public interface ITaskWorkflowService {
    List<TaskWorkflowDTO> getAllWorkflowsByTaskId(Integer id);
    TaskWorkflowPayload setStatus(TaskWorkflowPayload taskWorkflowPayload);
}
