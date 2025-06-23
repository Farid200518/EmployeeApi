package com.Task.EmployeeAPI.Services;

import com.Task.EmployeeAPI.DAO.Enums.Status;
import com.Task.EmployeeAPI.DTO.TaskWorkflowDTO;

public interface ITaskWorkflowService {
    TaskWorkflowDTO setToDone(Integer id, TaskWorkflowDTO taskWorkflowDTO);
    TaskWorkflowDTO setToInProgress(Integer id, TaskWorkflowDTO taskWorkflowDTO);
    TaskWorkflowDTO setToResolved(Integer id, TaskWorkflowDTO taskWorkflowDTO);
    TaskWorkflowDTO returnToInProgress(Integer id, TaskWorkflowDTO taskWorkflowDTO);
}
