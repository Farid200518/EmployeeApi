package com.Task.EmployeeAPI.Services;

import com.Task.EmployeeAPI.DAO.Entity.Employee;
import com.Task.EmployeeAPI.DAO.Entity.Task;
import com.Task.EmployeeAPI.DAO.Entity.TaskWorkflow;
import com.Task.EmployeeAPI.DAO.Enums.Status;
import com.Task.EmployeeAPI.DAO.Repository.EmployeeRepository;
import com.Task.EmployeeAPI.DAO.Repository.TaskRepository;
import com.Task.EmployeeAPI.DAO.Repository.TaskWorkflowRepository;
import com.Task.EmployeeAPI.DTO.EmployeeDTO;
import com.Task.EmployeeAPI.DTO.TaskDTO;
import com.Task.EmployeeAPI.DTO.TaskWorkflowDTO;
import com.Task.EmployeeAPI.Exceptions.BadRequestException;
import com.Task.EmployeeAPI.Exceptions.NotFoundException;
import com.Task.EmployeeAPI.Security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService{

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final TaskWorkflowService taskWorkflowService;
    private final TaskWorkflowRepository taskWorkflowRepository;
    private final ModelMapper modelMapper;


    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        if (taskDTO == null)
            throw new BadRequestException("Task input must not be null!");

        if (taskDTO.getEmployeeId() == null)
            throw new BadRequestException("Employee ID must not be null!");

        EmployeeDTO employeeDTO = employeeService
                .findEmployeeById(taskDTO.getEmployeeId());

        Employee employee = modelMapper.map(employeeDTO, Employee.class);

        // Employee that updates
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Employee updatedBy = employeeRepository.findByEmailAndIsDeletedFalse(userDetails.getEmail());

        // Saving Task
        Task task = modelMapper.map(taskDTO, Task.class);
        task.setEmployee(employee);
        taskRepository.save(task);

        // Task Workflow
        TaskWorkflow taskWorkflow = new TaskWorkflow();
        taskWorkflow.setTask(task);
        taskWorkflow.setStatus(Status.CREATED);
        taskWorkflow.setLastUpdated(LocalDateTime.now());
        taskWorkflow.setUpdatedBy(updatedBy);
        taskWorkflowRepository.save(taskWorkflow);

        return modelMapper.map(task, TaskDTO.class);
    }

    @Override
    public TaskDTO findTaskById(Integer id) {
        Task task = taskRepository
                .findByIdAndIsDeletedFalse(id);

        if (task == null) {
            throw new NotFoundException("Task with id " + id + " was not found!");
        }

        return modelMapper.map(task, TaskDTO.class);
    }

    @Override
    public List<TaskDTO> findAll() {
        return taskRepository
                .findByIsDeletedFalse()
                .stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .toList();
    }

    public List<TaskDTO> findAllEmployeeTasks(Integer id) {
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id);
        return taskRepository
                .findByEmployee_idAndIsDeletedFalse(employee.getId())
                .stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .toList();
    }

    @Override
    public TaskDTO deleteTaskById(Integer id) {
        Task task = taskRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Task with ID " + id + " doesn't exist!"));

        task.setDeleted(true);
        taskRepository.save(task);
        return modelMapper.map(task, TaskDTO.class);
    }

    @Override
    public TaskDTO updateTaskById(Integer id, TaskDTO taskDTO) {
        if (taskDTO == null) {
            throw new BadRequestException("Employee input must not be null");
        }

        Task task = taskRepository
                .findByIdAndIsDeletedFalse(id);

        if (task == null) {
            throw new NotFoundException("Task with ID " + id + " doesn't exist!");
        }

        EmployeeDTO employeeDTO = employeeService.findEmployeeById(taskDTO.getEmployeeId());
        Employee employee = modelMapper.map(employeeDTO, Employee.class);

        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setEmployee(employee);
        taskRepository.save(task);
        return modelMapper.map(task, TaskDTO.class);
    }
}
