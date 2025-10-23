package com.Task.employeeAPI.services.concrete;

import com.Task.employeeAPI.dao.Entity.Employee;
import com.Task.employeeAPI.dao.Entity.Task;
import com.Task.employeeAPI.dao.Entity.TaskWorkflow;
import com.Task.employeeAPI.dao.Enums.Status;
import com.Task.employeeAPI.dao.Repository.EmployeeRepository;
import com.Task.employeeAPI.dao.Repository.TaskRepository;
import com.Task.employeeAPI.dao.Repository.TaskWorkflowRepository;
import com.Task.employeeAPI.dto.EmployeeDTO;
import com.Task.employeeAPI.dto.NotificationDTO;
import com.Task.employeeAPI.dto.TaskDTO;
import com.Task.employeeAPI.exceptions.BadRequestException;
import com.Task.employeeAPI.exceptions.NotFoundException;
//import com.Task.employeeAPI.notification.NotificationProducer;
import com.Task.employeeAPI.security.CustomUserDetails;
import com.Task.employeeAPI.services.abstraction.ITaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final TaskWorkflowRepository taskWorkflowRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
//    private final NotificationProducer notificationProducer;


    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        if (taskDTO == null)
            throw new BadRequestException("Task input must not be null!");

        if (taskDTO.getEmployeeId() == null)
            throw new BadRequestException("Employee ID must not be null!");

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(taskDTO.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + taskDTO.getEmployeeId() + " doesn't exist!"));

        // Employee that updates
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Employee updatedBy = employeeRepository.findByEmailAndIsDeletedFalse(userDetails.getEmail());

        // Saving Task
        Task task = modelMapper.map(taskDTO, Task.class);
        task.setEmployee(employee);
        task.setStatus(Status.CREATED);
        taskRepository.save(task);

        // Task Workflow
        TaskWorkflow taskWorkflow = new TaskWorkflow();
        taskWorkflow.setTask(task);
        taskWorkflow.setStatus(Status.CREATED);
        taskWorkflow.setLastUpdated(LocalDateTime.now());
        taskWorkflow.setUpdatedBy(updatedBy);
        taskWorkflowRepository.save(taskWorkflow);

//        notificationProducer
//                .sendNotification(new NotificationDTO(
//                        employee.getEmail(),
//                        "You were assigned with new task",
//                        task.getDescription()));
//
        return modelMapper.map(task, TaskDTO.class);
    }

    @Override
    public TaskDTO findTaskById(Integer id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Task with id " + id + " was not found!"));

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
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Employee with ID " + id + " doesn't exist!"));
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

        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " was not found!"));

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(task.getEmployee().getId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + id + " doesn't exist!"));

        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setEmployee(employee);
        taskRepository.save(task);

//        notificationProducer
//                .sendNotification(new NotificationDTO(
//                        employee.getEmail(),
//                        "Task updated",
//                        task.getDescription()
//                ));
//
        return modelMapper.map(task, TaskDTO.class);
    }
}
