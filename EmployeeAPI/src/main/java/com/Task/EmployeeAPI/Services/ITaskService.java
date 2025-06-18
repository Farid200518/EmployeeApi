package com.Task.EmployeeAPI.Services;

import com.Task.EmployeeAPI.DTO.TaskDTO;

import java.util.List;

public interface ITaskService {
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO findTaskById(Integer id);
    List<TaskDTO> findAll();
    TaskDTO deleteTaskById(Integer id);
    TaskDTO updateTaskById(Integer id, TaskDTO taskDTO);
}
